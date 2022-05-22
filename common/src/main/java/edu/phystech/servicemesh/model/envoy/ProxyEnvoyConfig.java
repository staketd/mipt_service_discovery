package edu.phystech.servicemesh.model.envoy;

import java.util.List;
import java.util.Map;

import edu.phystech.servicemesh.model.Endpoint;
import edu.phystech.servicemesh.model.EnvoyType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.util.Pair;

@Getter
@Setter
@NoArgsConstructor
public class ProxyEnvoyConfig extends EnvoyConfig {
    private List<EnvoyMapping> endpointMappings;

    public ProxyEnvoyConfig(
            EnvoyId envoyId,
            Endpoint monitoringEndpoint,
            List<Pair<Endpoint, Endpoint>> endpointMappings,
            long version
    ) {
        super(envoyId, EnvoyType.INSTANCE, monitoringEndpoint, version);
        this.endpointMappings = endpointMappings.stream()
                .map(entry -> new EnvoyMapping(entry.getFirst(), entry.getSecond()))
                .toList();
    }
}
