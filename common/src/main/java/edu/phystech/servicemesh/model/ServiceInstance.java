package edu.phystech.servicemesh.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class ServiceInstance {
    private String nodeId;
    private Proxy proxy;
}
