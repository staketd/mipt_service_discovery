package edu.phystech.servicemesh.model.envoy;

import java.util.List;

import edu.phystech.servicemesh.model.Endpoint;
import edu.phystech.servicemesh.model.EnvoyType;
import lombok.Getter;

@Getter
public class BalancerEnvoyConfig extends EnvoyConfig {
    private final List<Endpoint> instances;

    public BalancerEnvoyConfig(
            EnvoyId envoyId,
            Endpoint monitoringEndpoint,
            List<Endpoint> instances
    ) {
        super(envoyId, EnvoyType.BALANCER, monitoringEndpoint);
        this.instances = instances;
    }
}
