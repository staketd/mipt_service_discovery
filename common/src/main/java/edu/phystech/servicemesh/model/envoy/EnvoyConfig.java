package edu.phystech.servicemesh.model.envoy;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.phystech.servicemesh.model.Endpoint;
import edu.phystech.servicemesh.model.EnvoyType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
public class EnvoyConfig {
    private EnvoyId envoyId;
    private EnvoyType type;
    private Endpoint monitoringEndpoint;

    public EnvoyConfig(EnvoyId envoyId, EnvoyType type, Endpoint monitoringEndpoint) {
        this.envoyId = envoyId;
        this.type = type;
        this.monitoringEndpoint = monitoringEndpoint;
    }
}
