package edu.phystech.servicemesh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.phystech.servicemesh.exception.ServiceInstanceNotExistsException;
import edu.phystech.servicemesh.exception.ServiceIsNotPresentOnNodeException;
import edu.phystech.servicemesh.model.AddressPortLayout;
import edu.phystech.servicemesh.model.ClientService;
import edu.phystech.servicemesh.model.Endpoint;
import edu.phystech.servicemesh.model.NodeLayout;
import edu.phystech.servicemesh.model.Proxy;
import edu.phystech.servicemesh.model.ServiceInstance;
import edu.phystech.servicemesh.model.ServiceInstanceId;
import edu.phystech.servicemesh.model.envoy.ChangeEnvoyConfigRequest;
import edu.phystech.servicemesh.model.envoy.EnvoyConfig;
import edu.phystech.servicemesh.model.envoy.EnvoyId;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class InstanceProcessor {
    private final ServiceDao serviceDao;
    private final NodeLayoutDao nodeLayoutDao;
    private final EnvoyService envoyService;
    private final EventNotifier eventNotifier;

    public InstanceProcessor(
            ServiceDao serviceDao,
            NodeLayoutDao nodeLayoutDao,
            EnvoyService envoyService,
            EventNotifier eventNotifier
    ) {
        this.serviceDao = serviceDao;
        this.nodeLayoutDao = nodeLayoutDao;
        this.envoyService = envoyService;
        this.eventNotifier = eventNotifier;
    }

    public ClientService moveInstance(String serviceId, ServiceInstanceId fromId, ServiceInstanceId toId) {
        Pair<ClientService, ChangeEnvoyConfigRequest> result = doMoveInstance(serviceId, fromId, toId);
        try {
            eventNotifier.sendNewServiceVersion(result.getRight());
        } catch (Exception e) {
            log.error("Failed to schedule config change, will try to schedule with scheduler", e);
        }
        return result.getLeft();
    }

    @Transactional(rollbackFor = Exception.class)
    public Pair<ClientService, ChangeEnvoyConfigRequest> doMoveInstance(String serviceId, ServiceInstanceId fromId, ServiceInstanceId toId) {
        ClientService currentVersion = serviceDao.getCurrentVersion(serviceId);
        NodeLayout fromNodeLayout = nodeLayoutDao.getNodeLayoutById(fromId.getNodeId());
        NodeLayout toNodeLayout = nodeLayoutDao.getNodeLayoutById(toId.getNodeId());

        ServiceInstance instance = currentVersion.getInstances()
                .stream()
                .filter(serviceInstance -> serviceInstance.getServiceInstanceId().equals(fromId))
                .findAny().orElseThrow(() -> new ServiceIsNotPresentOnNodeException(fromId));

        currentVersion.getInstances().remove(instance);

        deallocateInstance(fromNodeLayout, instance);

        ServiceInstance allocatedInstance =
                allocateServiceInstance(toNodeLayout, toId, currentVersion.getUsedServices(), currentVersion.getPort());

        currentVersion.getInstances().add(
                allocatedInstance
        );

        ChangeEnvoyConfigRequest request = new ChangeEnvoyConfigRequest(
                serviceId,
                currentVersion.getVersion() + 1,
                List.of(
                        currentVersion.getBalancerEnvoyConfig(),
                        envoyService.getProxyEnvoyConfig(currentVersion, EnvoyId.getInstanceId(serviceId,
                                instance.getServiceInstanceId().getNodeId(), instance.getServiceInstanceId().getId()))
                )
        );

        nodeLayoutDao.saveNodeLayouts(List.of(fromNodeLayout, toNodeLayout));
        return Pair.of(serviceDao.saveNewVersion(currentVersion), request);
    }

    public HashMap<String, Integer> getPortMappingForService(Collection<String> services) {
        HashMap<String, Integer> portMapping = new HashMap<>();
        int startPort = AddressPortLayout.MIN_PORT;
        for (String serviceId : services) {
            portMapping.put(serviceId, startPort++);
        }
        return portMapping;
    }

    public ClientService addInstances(String serviceId, List<ServiceInstanceId> serviceInstanceIds) {
        Pair<ClientService, ChangeEnvoyConfigRequest> result = doAddInstances(serviceId, serviceInstanceIds);
        try {
            eventNotifier.sendNewServiceVersion(result.getRight());
        } catch (Exception e) {
            log.error("Failed to notify", e);
        }
        return result.getLeft();
    }

    @Transactional(rollbackFor = Exception.class)
    protected Pair<ClientService, ChangeEnvoyConfigRequest> doAddInstances(String serviceId, List<ServiceInstanceId> serviceInstanceIds) {
        ClientService service = serviceDao.getCurrentVersion(serviceId);
        ClientService newVersion = addInstances(service, serviceInstanceIds);

        HashSet<ServiceInstanceId> instanceIds = new HashSet<>(serviceInstanceIds);
        List<ServiceInstance> addedInstances = newVersion.getInstances().stream()
                .filter(instance -> instanceIds.contains(instance.getServiceInstanceId()))
                .toList();

        List<ClientService> usedService = serviceDao.getByIds(newVersion.getUsedServices());
        List<EnvoyConfig> envoyConfigs = new ArrayList<>(envoyService.getInstancesEnvoyConfigs(serviceId, usedService, addedInstances));
        envoyConfigs.add(newVersion.getBalancerEnvoyConfig());
        return Pair.of(newVersion,
                new ChangeEnvoyConfigRequest(
                        serviceId,
                        newVersion.getVersion(),
                        envoyConfigs
                )
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public ClientService addInstances(ClientService service, List<ServiceInstanceId> serviceInstanceIds) {
        Set<String> nodeIds = serviceInstanceIds.stream().map(ServiceInstanceId::getNodeId).collect(Collectors.toSet());
        Map<String, NodeLayout> nodeLayouts = nodeLayoutDao.getLayouts(nodeIds);
        allocateServiceInstances(service, serviceInstanceIds, nodeLayouts);
        nodeLayoutDao.saveNodeLayouts(nodeLayouts.values());
        return serviceDao.saveNewVersion(service);
    }

    private void allocateServiceInstances(
            ClientService service,
            List<ServiceInstanceId> serviceInstanceIds,
            Map<String, NodeLayout> nodeLayouts
    ) {
        List<ServiceInstance> instances = serviceInstanceIds.stream()
                .map(instanceId ->
                        allocateServiceInstance(
                                nodeLayouts.get(instanceId.getNodeId()),
                                instanceId,
                                service.getUsedServices(),
                                service.getPort())
                ).toList();

        service.getInstances().addAll(instances);
    }

    private ServiceInstance allocateServiceInstance(
            NodeLayout layout,
            ServiceInstanceId serviceInstanceId,
            Set<String> usedServices,
            int servicePort
    ) {
        Endpoint monitoringEndpoint = layout.allocateIngressEndpoint();
        Endpoint serviceIngressEndpoint = layout.allocateIngressEndpoint();
        String serviceLocalAddress = layout.allocateLocalAddress();
        String proxyLocalAddress = layout.allocateLocalAddress();

        Map<String, Integer> egressEndpointsPorts = getPortMappingForService(usedServices);

        return new ServiceInstance(
                serviceInstanceId,
                new Endpoint(serviceLocalAddress, servicePort),
                new Proxy(serviceIngressEndpoint, monitoringEndpoint, proxyLocalAddress),
                egressEndpointsPorts
        );
    }

    public ClientService deallocateInstances(String serviceId, List<ServiceInstanceId> serviceInstanceIds) {
        ClientService newVersion = doDeallocateInstances(serviceId, serviceInstanceIds);
        ChangeEnvoyConfigRequest request = new ChangeEnvoyConfigRequest(
                serviceId,
                newVersion.getVersion(),
                List.of(newVersion.getBalancerEnvoyConfig())
        );
        try {
            eventNotifier.sendNewServiceVersion(request);
        } catch (Exception e) {
            log.error("Failed to notify", e);
        }
        return newVersion;
    }

    @Transactional(rollbackFor = Exception.class)
    public ClientService doDeallocateInstances(String serviceId, List<ServiceInstanceId> serviceInstanceIds) {
        ClientService service = serviceDao.getCurrentVersion(serviceId);

        Map<ServiceInstanceId, ServiceInstance> serviceInstanceMap = service.getInstances().stream()
                        .collect(Collectors.toMap(ServiceInstance::getServiceInstanceId, Function.identity()));

        List<ServiceInstance> servicesToDelete = serviceInstanceIds.stream()
                .map(serviceInstanceId -> {
                    ServiceInstance instance = serviceInstanceMap.get(serviceInstanceId);
                    if (instance == null) {
                        throw new ServiceInstanceNotExistsException(serviceId, serviceInstanceId);
                    }
                    return instance;
                })
                .toList();

        return deallocateInstances(service, servicesToDelete);
    }

    public ClientService deallocateInstances(ClientService service, List<ServiceInstance> serviceInstances) {
        Map<String, NodeLayout> nodeLayouts = nodeLayoutDao.getLayouts(
                serviceInstances.stream()
                        .map(ServiceInstance::getServiceInstanceId)
                        .map(ServiceInstanceId::getNodeId)
                        .toList()
        );

        return deallocateInstances(service, nodeLayouts, serviceInstances);
    }

    private ClientService deallocateInstances(
            ClientService service,
            Map<String, NodeLayout> nodeLayouts,
            List<ServiceInstance> serviceInstances
    ) {
        serviceInstances.forEach(serviceInstance ->
                deallocateInstance(nodeLayouts.get(serviceInstance.getServiceInstanceId().getNodeId()), serviceInstance)
        );
        service.getInstances().removeAll(serviceInstances);

        nodeLayoutDao.saveNodeLayouts(nodeLayouts.values());

        return serviceDao.saveNewVersion(service);
    }

    private void deallocateInstance(NodeLayout nodeLayout, ServiceInstance instance) {
        nodeLayout.deallocateIngressEndpoint(instance.getProxy().getIngressEndpoint());
        nodeLayout.deallocateIngressEndpoint(instance.getProxy().getMonitoringEndpoint());
        nodeLayout.deallocateLocalAddress(instance.getProxy().getLocalAddress());
        nodeLayout.deallocateLocalAddress(instance.getLocalEndpoint().getAddress());
    }
}
