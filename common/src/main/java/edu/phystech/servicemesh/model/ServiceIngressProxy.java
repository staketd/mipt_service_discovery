package edu.phystech.servicemesh.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class ServiceIngressProxy {
    private String nodeId;
    private Endpoint ingressEndpoint;
    private Endpoint monitoringEndpoint;
}
