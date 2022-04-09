package edu.phystech.servicemesh;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.phystech.servicemesh.exception.ServiceAlreadyExistsException;
import edu.phystech.servicemesh.model.ClientService;
import edu.phystech.servicemesh.model.Endpoint;
import edu.phystech.servicemesh.model.Node;
import edu.phystech.servicemesh.model.NodeLayout;
import edu.phystech.servicemesh.model.Proxy;
import edu.phystech.servicemesh.model.ServiceIngressProxy;
import edu.phystech.servicemesh.model.ServiceInstance;
import edu.phystech.servicemesh.request.CreateServiceRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientServiceProcessor {
    private final ServiceDao serviceDao;
    private final NodeLayoutDao nodeLayoutDao;

    public ClientServiceProcessor(
            ServiceDao serviceDao,
            NodeLayoutDao nodeLayoutDao
    ) {
        this.serviceDao = serviceDao;
        this.nodeLayoutDao = nodeLayoutDao;
    }

    @Transactional
    public ClientService createService(CreateServiceRequest request) {
        if (serviceDao.exists(request.getServiceId())) {
            throw new ServiceAlreadyExistsException(request.getServiceId());
        }
        Map<String, NodeLayout> nodeLayouts = createOrGetNodeLayout(request);

        ClientService clientService = new ClientService(request.getServiceId(), request.getServiceName(),
                request.getUsedServices());

        request.getUsedServices().forEach(service -> addUsedByService(service, request.getServiceId()));

        Endpoint ingressProxyEndpoint =
                nodeLayouts.get(request.getServiceIngressNode().getNodeIdentifier()).allocateIngressEndpoint();

        clientService.setServiceIngressProxy(new ServiceIngressProxy(ingressProxyEndpoint));

        allocateServiceEndpoints(clientService, nodeLayouts);

        nodeLayoutDao.saveNodeLayouts(nodeLayouts.values());

        serviceDao.saveNewVersion(clientService);

        return clientService;
    }

    private Map<String, NodeLayout> createOrGetNodeLayout(CreateServiceRequest request) {
        Map<String, Node> nodes = request.getInstanceNodes().stream()
                .collect(Collectors.toMap(Node::getNodeIdentifier, Function.identity()));
        nodes.put(request.getServiceIngressNode().getNodeIdentifier(), request.getServiceIngressNode());
        Map<String, NodeLayout> layouts = nodeLayoutDao.getExistingLayouts(nodes.keySet());
        nodes.keySet().forEach(nodeId -> layouts.putIfAbsent(nodeId, new NodeLayout(nodeId,
                nodes.get(nodeId).getAvailableAddresses())));
        return layouts;
    }

    private void allocateServiceEndpoints(ClientService service, Map<String, NodeLayout> nodeLayouts) {
        List<ServiceInstance> instances = nodeLayouts.values().stream()
                .map(layout -> allocateServiceInstance(layout, service.getUsedServices()))
                .toList();
        service.setInstances(instances);
    }

    private ServiceInstance allocateServiceInstance(NodeLayout layout, Set<String> usedServices) {
        Endpoint monitoringEndpoint = layout.allocateIngressEndpoint();
        Endpoint serviceIngressEndpoint = layout.allocateIngressEndpoint();
        Map<String, Endpoint> egressEndpoints = usedServices.stream()
                .map(serviceId -> Map.entry(serviceId, layout.allocateEgressEndpoint()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new ServiceInstance(
                layout.getNodeIdentifier(),
                new Proxy(serviceIngressEndpoint, monitoringEndpoint, egressEndpoints)
        );
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

    @Transactional
    public ClientService editMeta(String serviceId, String newName, String fqdn) {
        ClientService currentVersion = serviceDao.getCurrentVersion(serviceId);
        currentVersion.setName(newName);
        return serviceDao.saveNewVersion(currentVersion);
    }

    @Transactional
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

        return serviceDao.saveNewVersion(currentVersion);
    }

    public List<ClientService> getServices() {
        return serviceDao.getAllServices();
    }
}
