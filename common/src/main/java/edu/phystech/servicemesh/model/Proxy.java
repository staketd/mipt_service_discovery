package edu.phystech.servicemesh.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@AllArgsConstructor
@Setter
@Getter
public class Proxy {
    private Endpoint ingressEndpoint;
    private Endpoint monitoringEndpoint;
    private Map<String, Endpoint> egressEndpoints;
}
