package edu.phystech.servicemesh;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.phystech.servicemesh.exception.CommonApiException;
import edu.phystech.servicemesh.exception.ServiceAlreadyExistsException;
import edu.phystech.servicemesh.model.ClientService;
import edu.phystech.servicemesh.model.Endpoint;
import edu.phystech.servicemesh.model.NodeLayout;
import edu.phystech.servicemesh.model.ServiceIngressProxy;
import edu.phystech.servicemesh.model.request.CreateServiceRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientServiceProcessor {
    private final ServiceDao serviceDao;
    private final NodeLayoutDao nodeLayoutDao;
    private final InstanceProcessor instanceProcessor;

    public ClientServiceProcessor(
            ServiceDao serviceDao,
            NodeLayoutDao nodeLayoutDao,
            InstanceProcessor instanceProcessor
    ) {
        this.serviceDao = serviceDao;
        this.nodeLayoutDao = nodeLayoutDao;
        this.instanceProcessor = instanceProcessor;
    }

    @Transactional(rollbackFor = Exception.class)
    public ClientService createService(CreateServiceRequest request) {
        if (serviceDao.exists(request.getServiceId())) {
            throw new ServiceAlreadyExistsException(request.getServiceId());
        }

        NodeLayout balancerNodeLayout = nodeLayoutDao.getNodeLayoutById(request.getServiceIngressNodeId());

        ClientService clientService = new ClientService(request);

        request.getUsedServices().forEach(service -> addUsedByService(service, request.getServiceId()));

        Endpoint ingressProxyEndpoint = balancerNodeLayout.allocateIngressEndpoint();
        Endpoint monitoringProxyEndpoint = balancerNodeLayout.allocateIngressEndpoint();

        nodeLayoutDao.saveNodeLayout(balancerNodeLayout);

        clientService.setServiceIngressProxy(new ServiceIngressProxy(
                request.getServiceIngressNodeId(),
                ingressProxyEndpoint,
                monitoringProxyEndpoint
        ));

        instanceProcessor.addInstances(clientService, request.getServiceInstanceIds());

        serviceDao.saveNewVersion(clientService);

        return clientService;
    }

    private void addUsedByService(String serviceId, String usedByService) {
        ClientService service = serviceDao.getCurrentVersion(serviceId);
        service.getUsedByServices().add(usedByService);
        serviceDao.saveNewVersion(service);
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

    @Transactional(rollbackFor = Exception.class)
    public ClientService changeUsedService(String serviceId, Set<String> usedServiceIds) {
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
        } else  {
            currentVersion.getUsedByServices().remove(serviceId);
        }

        currentVersion.setUsedServices(usedServiceIds);
        reallocateLocalProxyPorts(currentVersion);

        return serviceDao.saveNewVersion(currentVersion);
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
}
