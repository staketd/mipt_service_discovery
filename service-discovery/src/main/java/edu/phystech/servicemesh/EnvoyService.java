package edu.phystech.servicemesh;

import edu.phystech.servicemesh.model.ClientService;
import edu.phystech.servicemesh.model.Endpoint;
import edu.phystech.servicemesh.model.ServiceInstance;
import edu.phystech.servicemesh.model.ServiceInstanceId;
import edu.phystech.servicemesh.model.envoy.EnvoyConfig;
import edu.phystech.servicemesh.model.envoy.EnvoyId;
import edu.phystech.servicemesh.model.envoy.ProxyEnvoyConfig;
import org.hibernate.ObjectNotFoundException;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EnvoyService {
    private final ServiceDao serviceDao;

    public EnvoyService(ServiceDao serviceDao) {
        this.serviceDao = serviceDao;
    }

    @Transactional(readOnly = true)
    public EnvoyConfig getEnvoyConfig(String serviceId, EnvoyId envoyId) {
        ClientService service = serviceDao.getCurrentVersion(serviceId);
        return switch (envoyId.getEnvoyType()) {
            case BALANCER -> getBalancerEnvoyConfig(service);
            case INSTANCE -> getProxyEnvoyConfig(service, envoyId);
        };
    }

    private EnvoyConfig getBalancerEnvoyConfig(ClientService service) {
        return service.getBalancerEnvoyConfig();
    }

    public ProxyEnvoyConfig getProxyEnvoyConfig(ClientService service, EnvoyId envoyId) {
        ServiceInstanceId instanceId = envoyId.getServiceInstanceId();

        List<ClientService> usedServices = serviceDao.getByIds(service.getUsedServices());

        ServiceInstance instance = service.getInstances().stream()
                .filter(serviceInstance -> serviceInstance.getServiceInstanceId().equals(instanceId))
                .findFirst().orElseThrow(() -> new ObjectNotFoundException(envoyId, "envoy"));

        return getProxyEnvoyConfig(service, usedServices, instance);
    }

    public List<EnvoyConfig> getInstancesEnvoyConfigs(
            ClientService service,
            List<ClientService> usedServices,
            List<ServiceInstance> instances
    ) {
        return instances.stream().map(instance -> (EnvoyConfig) getProxyEnvoyConfig(service, usedServices, instance)).toList();
    }

    public ProxyEnvoyConfig getProxyEnvoyConfig(
            ClientService service,
            List<ClientService> usedServices,
            ServiceInstance instance) {
        List<Pair<Endpoint, Endpoint>> endpointMapping = new ArrayList<>();

        endpointMapping.add(Pair.of(instance.getProxy().getIngressEndpoint(), instance.getLocalEndpoint()));

        usedServices.forEach(
                usedService -> endpointMapping.add(Pair.of(
                            new Endpoint(
                                    instance.getProxy().getLocalAddress(),
                                    instance.getEgressEndpointsPorts().get(usedService.getServiceId())
                            ),
                            usedService.getServiceIngressProxy().getIngressEndpoint()
                        )
                )
        );

        return new ProxyEnvoyConfig(
                EnvoyId.getInstanceId(service.getServiceId(), instance.getServiceInstanceId().getNodeId(), instance.getServiceInstanceId().getId()),
                instance.getProxy().getMonitoringEndpoint(),
                endpointMapping,
                service.getVersion()
        );
    }
}
