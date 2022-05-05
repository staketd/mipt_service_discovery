package edu.phystech.servicemesh.model;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.phystech.servicemesh.model.envoy.EnvoyId;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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
