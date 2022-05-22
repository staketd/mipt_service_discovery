package edu.phystech.servicemesh.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.phystech.servicemesh.model.envoy.EnvoyConfig;
import edu.phystech.servicemesh.model.envoy.EnvoyId;
import edu.phystech.servicemesh.model.envoy.ProxyEnvoyConfig;
import edu.phystech.servicemesh.model.request.CreateServiceRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.util.Pair;

@Document(collection = "services")
@Getter
@Setter
@NoArgsConstructor
public class ClientService {
    @MongoId
    private String serviceId;

    private String name;
    private int port;

    private long version;
    private long maxDeployedVersion;

    private ServiceIngressProxy serviceIngressProxy;
    private List<ServiceInstance> instances;

    private Set<String> usedByServices = new HashSet<>();
    private Set<String> usedServices;

    public ClientService(CreateServiceRequest request) {
        this.serviceId = request.getServiceId();
        this.name = request.getServiceName();
        this.version = 0;
        this.maxDeployedVersion = 0;
        this.usedServices = request.getUsedServices();
        this.port = request.getPort();
        this.instances = new LinkedList<>();
    }

    @JsonIgnore
    public EnvoyId getBalancerEnvoyId() {
        return EnvoyId.getBalancerId(serviceId, serviceIngressProxy.getNodeId());
    }

    @JsonIgnore
    public List<Endpoint> getInstancesEndpoints() {
        return instances.stream().map(instance -> instance.getProxy().getIngressEndpoint()).toList();
    }

    @JsonIgnore
    public EnvoyConfig getBalancerEnvoyConfig() {
        return new ProxyEnvoyConfig(
                getBalancerEnvoyId(),
                serviceIngressProxy.getMonitoringEndpoint(),
                instances.stream().map(instance -> Pair.of(
                        serviceIngressProxy.getIngressEndpoint(),
                        instance.getProxy().getIngressEndpoint()
                    )
                ).toList(),
                version
        );
    }
}
