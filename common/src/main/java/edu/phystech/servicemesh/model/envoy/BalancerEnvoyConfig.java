package edu.phystech.servicemesh.model.envoy;

import edu.phystech.servicemesh.model.Endpoint;
import edu.phystech.servicemesh.model.EnvoyType;
import lombok.Getter;

import java.util.List;

@Getter
public class BalancerEnvoyConfig extends EnvoyConfig {
    private final List<Endpoint> instances;

    public BalancerEnvoyConfig(
            EnvoyId envoyId,
            Endpoint monitoringEndpoint,
            List<Endpoint> instances,
            long version
    ) {
        super(envoyId, EnvoyType.BALANCER, monitoringEndpoint, version);
        this.instances = instances;
    }
}
