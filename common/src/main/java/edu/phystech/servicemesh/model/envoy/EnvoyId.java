package edu.phystech.servicemesh.model.envoy;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnvoyId implements Serializable {
    private String clusterId;
    private String nodeId;
}
