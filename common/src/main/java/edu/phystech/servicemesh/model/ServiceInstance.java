package edu.phystech.servicemesh.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;


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
