package edu.phystech.servicemesh;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.phystech.servicemesh.model.ClientService;
import edu.phystech.servicemesh.model.Endpoint;
import edu.phystech.servicemesh.model.EnvoyType;
import edu.phystech.servicemesh.model.ServiceIngressProxy;
import edu.phystech.servicemesh.model.ServiceInstance;
import edu.phystech.servicemesh.model.ServiceInstanceId;
import edu.phystech.servicemesh.model.envoy.BalancerEnvoyConfig;
import edu.phystech.servicemesh.model.envoy.EnvoyConfig;
import edu.phystech.servicemesh.model.envoy.EnvoyId;
import edu.phystech.servicemesh.model.envoy.ProxyEnvoyConfig;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnvoyService {
    private final ServiceDao serviceDao;

    public EnvoyService(ServiceDao serviceDao) {
        this.serviceDao = serviceDao;
    }

    @Transactional(readOnly = true)
    public EnvoyConfig getEnvoyConfig(String serviceId, EnvoyType envoyType, EnvoyId envoyId) {
        ClientService service = serviceDao.getCurrentVersion(serviceId);
        return switch (envoyType) {
            case BALANCER -> getBalancerEnvoyConfig(service);
            case INSTANCE -> getProxyEnvoyConfig(service, envoyId);
        };
    }

    private BalancerEnvoyConfig getBalancerEnvoyConfig(ClientService service) {
        ServiceIngressProxy proxy = service.getServiceIngressProxy();
        EnvoyId envoyId = new EnvoyId(proxy.getNodeId(), service.getServiceId());
        return new BalancerEnvoyConfig(
                envoyId,
                proxy.getMonitoringEndpoint(),
                service.getInstances()
                        .stream()
                        .map(instance -> instance.getProxy().getIngressEndpoint())
                        .toList()
        );
    }

    public ProxyEnvoyConfig getProxyEnvoyConfig(ClientService service, EnvoyId envoyId) {
        ServiceInstanceId instanceId = new ServiceInstanceId(envoyId.getNodeId(), envoyId.getClusterId());

        List<ClientService> usedServices = serviceDao.getByIds(service.getUsedServices());

        ServiceInstance instance = service.getInstances().stream()
                .filter(serviceInstance -> serviceInstance.getServiceInstanceId().equals(instanceId))
                .findFirst().orElseThrow(() -> new ObjectNotFoundException(envoyId, "envoy"));

        return getProxyEnvoyConfig(usedServices, instance);
    }

    public List<EnvoyConfig> getInstancesEnvoyConfigs(
            List<ClientService> usedServices,
            List<ServiceInstance> instances
    ) {
        return instances.stream().map(instance -> (EnvoyConfig) getProxyEnvoyConfig(usedServices, instance)).toList();
    }

    public ProxyEnvoyConfig getProxyEnvoyConfig(
            List<ClientService> usedServices,
            ServiceInstance instance) {
        Map<Endpoint, Endpoint> endpointMapping = new HashMap<>();

        endpointMapping.put(instance.getProxy().getIngressEndpoint(), instance.getLocalEndpoint());

        usedServices.forEach(
                usedService -> endpointMapping.put(
                        new Endpoint(
                                instance.getProxy().getLocalAddress(),
                                instance.getEgressEndpointsPorts().get(usedService.getServiceId())
                        ),
                        usedService.getServiceIngressProxy().getIngressEndpoint()
                )
        );

        return new ProxyEnvoyConfig(
                instance.getServiceInstanceId().getEnvoyId(),
                instance.getProxy().getMonitoringEndpoint(),
                endpointMapping
        );
    }
}
