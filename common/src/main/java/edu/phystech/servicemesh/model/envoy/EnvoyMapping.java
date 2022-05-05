package edu.phystech.servicemesh.model.envoy;

import edu.phystech.servicemesh.model.Endpoint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnvoyMapping {
    private Endpoint from;
    private Endpoint to;
}
