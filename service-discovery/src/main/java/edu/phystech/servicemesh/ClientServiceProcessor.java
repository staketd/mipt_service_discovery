package edu.phystech.servicemesh;

import edu.phystech.servicemesh.exception.CommonApiException;
import edu.phystech.servicemesh.exception.ServiceAlreadyExistsException;
import edu.phystech.servicemesh.model.*;
import edu.phystech.servicemesh.model.envoy.ChangeEnvoyConfigRequest;
import edu.phystech.servicemesh.model.envoy.EnvoyConfig;
import edu.phystech.servicemesh.model.request.CreateServiceRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClientServiceProcessor {
    private final ServiceDao serviceDao;
    private final NodeLayoutDao nodeLayoutDao;
    private final InstanceProcessor instanceProcessor;
    private final EnvoyService envoyService;
    private final EventNotifier eventNotifier;

    public ClientServiceProcessor(
            ServiceDao serviceDao,
            NodeLayoutDao nodeLayoutDao,
            InstanceProcessor instanceProcessor,
            EnvoyService envoyService,
            EventNotifier eventNotifier) {
        this.serviceDao = serviceDao;
        this.nodeLayoutDao = nodeLayoutDao;
        this.instanceProcessor = instanceProcessor;
        this.envoyService = envoyService;
        this.eventNotifier = eventNotifier;
    }

    public ClientService createService(CreateServiceRequest request) {
        Pair<ClientService, ChangeEnvoyConfigRequest> result = doCreateService(request);
        try {
            eventNotifier.sendNewServiceVersion(result.getRight());
        } catch (Exception e) {
            log.error("failed to send notify", e);
        }
        return result.getLeft();
    }

    @Transactional
    public Pair<ClientService, ChangeEnvoyConfigRequest> doCreateService(CreateServiceRequest request) {
        if (serviceDao.exists(request.getServiceId())) {
            throw new ServiceAlreadyExistsException(request.getServiceId());
        }

        NodeLayout balancerNodeLayout = nodeLayoutDao.getNodeLayoutById(request.getServiceIngressNodeId());

        ClientService clientService = new ClientService(request);

        List<ClientService> usedServices = request.getUsedServices()
                .stream()
                .map(service -> addUsedByService(service, request.getServiceId()))
                .toList();

        Endpoint ingressProxyEndpoint = balancerNodeLayout.allocateIngressEndpoint();
        Endpoint monitoringProxyEndpoint = balancerNodeLayout.allocateIngressEndpoint();

        nodeLayoutDao.saveNodeLayout(balancerNodeLayout);

        clientService.setServiceIngressProxy(new Proxy(
                ingressProxyEndpoint,
                monitoringProxyEndpoint,
                request.getServiceIngressNodeId(),
                null
        ));

        instanceProcessor.addInstances(clientService, request.getServiceInstanceIds());
        ClientService newVersion = serviceDao.saveNewVersion(clientService);

        List<EnvoyConfig> envoyConfigs = new LinkedList<>(clientService.getInstances().stream()
                .map(instance -> (EnvoyConfig) envoyService.getProxyEnvoyConfig(
                        newVersion,
                        usedServices,
                        instance
                ))
                .toList()
        );

        envoyConfigs.add(newVersion.getBalancerEnvoyConfig());

        return Pair.of(newVersion,
                new ChangeEnvoyConfigRequest(
                        newVersion.getServiceId(),
                        newVersion.getVersion(),
                        envoyConfigs
                )
        );
    }

    private ClientService addUsedByService(String serviceId, String usedByService) {
        ClientService service = serviceDao.getCurrentVersion(serviceId);
        service.getUsedByServices().add(usedByService);
        return serviceDao.saveNewVersion(service);
    }

    public ClientService getServiceByServiceIdAndVersion(String serviceId, long version) {
        return serviceDao.getByServiceIdAndVersion(serviceId, version);
    }

    public ClientService getCurrentServiceVersion(String serviceId) {
        return serviceDao.getCurrentVersion(serviceId);
    }

    @Transactional(rollbackFor = Exception.class)
    public ClientService editMeta(String serviceId, String newName, String fqdn) {
        ClientService currentVersion = serviceDao.getCurrentVersion(serviceId);
        currentVersion.setName(newName);
        return serviceDao.saveNewVersion(currentVersion);
    }

    public ClientService changeUsedService(String serviceId, Set<String> usedServiceIds) {
        Pair<ClientService, ChangeEnvoyConfigRequest> result = doChangeUsedService(serviceId, usedServiceIds);
        try {
            eventNotifier.sendNewServiceVersion(result.getRight());
        } catch (Exception e) {
            log.error("Failed to notify", e);
        }
        return result.getLeft();
    }

    @Transactional(rollbackFor = Exception.class)
    public Pair<ClientService, ChangeEnvoyConfigRequest> doChangeUsedService(String serviceId,
                                                                             Set<String> usedServiceIdsImmutable) {
        Set<String> usedServiceIds = new HashSet<>(usedServiceIdsImmutable);
        boolean selfUsing = usedServiceIds.contains(serviceId);
        usedServiceIds.remove(serviceId);
        ClientService currentVersion = serviceDao.getCurrentVersion(serviceId);
        Map<String, ClientService> previousUsedServices = serviceDao.getByIds(currentVersion.getUsedServices()).stream()
                .peek(service -> {
                    if (!usedServiceIds.contains(service.getServiceId())) {
                        service.getUsedByServices().remove(serviceId);
                    }
                })
                .collect(Collectors.toMap(ClientService::getServiceId, Function.identity()));

        previousUsedServices.putAll(
                serviceDao.getByIds(
                                usedServiceIds.stream()
                                        .filter(service -> !previousUsedServices.containsKey(service))
                                        .toList()
                        ).stream().peek(service -> service.getUsedByServices().add(serviceId))
                        .collect(Collectors.toMap(ClientService::getServiceId, Function.identity()))
        );

        previousUsedServices.values().forEach(serviceDao::saveNewVersion);

        if (selfUsing) {
            usedServiceIds.add(serviceId);
            currentVersion.getUsedByServices().add(serviceId);
        } else {
            currentVersion.getUsedByServices().remove(serviceId);
        }

        currentVersion.setUsedServices(usedServiceIds);
        reallocateLocalProxyPorts(currentVersion);

        ClientService newVersion = serviceDao.saveNewVersion(currentVersion);

        List<ClientService> usedServices = usedServiceIds.stream().map(previousUsedServices::get).toList();

        ChangeEnvoyConfigRequest request =
                new ChangeEnvoyConfigRequest(
                        serviceId,
                        newVersion.getVersion(),
                        newVersion.getInstances().stream()
                                .map(instance -> (EnvoyConfig) envoyService.getProxyEnvoyConfig(newVersion, usedServices, instance))
                                .toList()
                );

        return Pair.of(newVersion, request);
    }

    private void reallocateLocalProxyPorts(ClientService service) {
        Map<String, Integer> portMapping = instanceProcessor.getPortMappingForService(service.getUsedServices());

        service.getInstances().forEach(serviceInstance -> serviceInstance.setEgressEndpointsPorts(portMapping));
    }

    public List<ClientService> getServices() {
        return serviceDao.getAllServices();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteService(String serviceId) {
        ClientService service = serviceDao.getCurrentVersion(serviceId);

        if (!service.getUsedByServices().isEmpty()) {
            throw new CommonApiException("Can't delete service used by other services");
        }

        instanceProcessor.deallocateInstances(service, service.getInstances());

        NodeLayout balancerNodeLayout = nodeLayoutDao.getNodeLayoutById(service.getServiceIngressProxy().getNodeId());

        balancerNodeLayout.deallocateIngressEndpoint(service.getServiceIngressProxy().getIngressEndpoint());
        balancerNodeLayout.deallocateIngressEndpoint(service.getServiceIngressProxy().getMonitoringEndpoint());

        nodeLayoutDao.saveNodeLayout(balancerNodeLayout);

        changeUsedService(serviceId, Set.of());
        serviceDao.deleteService(serviceId);
    }

    public ClientService moveBalancer(String serviceId, String nodeId) {
        Pair<ClientService, List<ChangeEnvoyConfigRequest>> result = doMoveBalancer(serviceId, nodeId);

        try {
            result.getValue().forEach(eventNotifier::sendNewServiceVersion);
        } catch (Exception e) {
            log.error("Failed to notify", e);
        }

        return result.getKey();
    }

    public Pair<ClientService, List<ChangeEnvoyConfigRequest>> doMoveBalancer(String serviceId, String nodeId) {
        ClientService service = serviceDao.getCurrentVersion(serviceId);
        List<ClientService> usedByServices = serviceDao.getByIds(service.getUsedByServices());

        NodeLayout balancerOldNodeLayout =
                nodeLayoutDao.getNodeLayoutById(service.getServiceIngressProxy().getNodeId());

        balancerOldNodeLayout.deallocateIngressEndpoint(service.getServiceIngressProxy().getIngressEndpoint());
        balancerOldNodeLayout.deallocateIngressEndpoint(service.getServiceIngressProxy().getMonitoringEndpoint());

        NodeLayout balancerNewNodeLayout = nodeLayoutDao.getNodeLayoutById(nodeId);

        Endpoint ingressEndpoint = balancerNewNodeLayout.allocateIngressEndpoint();
        Endpoint monitoringEndpoint = balancerNewNodeLayout.allocateIngressEndpoint();

        nodeLayoutDao.saveNodeLayouts(List.of(balancerOldNodeLayout, balancerNewNodeLayout));
        service.setServiceIngressProxy(new Proxy(ingressEndpoint, monitoringEndpoint, nodeId, null));

        ClientService newVersion = serviceDao.saveNewVersion(service);

        List<ChangeEnvoyConfigRequest> requests = new ArrayList<>();

        requests.add(
                new ChangeEnvoyConfigRequest(
                        serviceId,
                        newVersion.getVersion(),
                        List.of(newVersion.getBalancerEnvoyConfig())
                )
        );

        requests.addAll(usedByServices.stream().map(usedByService -> {
                    ClientService serviceNewVersion = serviceDao.saveNewVersion(usedByService);
                    List<ClientService> usedServices = serviceDao.getByIds(serviceNewVersion.getUsedServices());
                    return new ChangeEnvoyConfigRequest(
                            serviceNewVersion.getServiceId(),
                            serviceNewVersion.getVersion(),
                            envoyService.getInstancesEnvoyConfigs(newVersion, usedServices,
                                    serviceNewVersion.getInstances())
                    );
                }).toList()
        );

        return Pair.of(newVersion, requests);
    }
}
