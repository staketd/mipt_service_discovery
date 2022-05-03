package edu.phystech.servicemesh.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import edu.phystech.servicemesh.ServiceInstanceId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ServiceInstance {
    @JsonUnwrapped
    private ServiceInstanceId serviceInstanceId;

    private Endpoint localEndpoint;
    private Proxy proxy;

    private Map<String, Integer> egressEndpointsPorts;
}
