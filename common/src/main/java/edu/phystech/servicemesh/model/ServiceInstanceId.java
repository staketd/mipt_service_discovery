package edu.phystech.servicemesh.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.phystech.servicemesh.model.envoy.EnvoyId;
import lombok.*;

import javax.validation.constraints.NotEmpty;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ServiceInstanceId {
    @NotEmpty
    private String id;
    @NotEmpty
    private String nodeId;

    @JsonIgnore
    public EnvoyId getEnvoyId() {
        return new EnvoyId(nodeId, id);
    }
}
