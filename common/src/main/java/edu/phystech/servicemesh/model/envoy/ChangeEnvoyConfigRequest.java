package edu.phystech.servicemesh.model.envoy;


import java.util.List;


public record ChangeEnvoyConfigRequest(
        String serviceId,
        long version,
        List<EnvoyConfig> configs
) {
}
