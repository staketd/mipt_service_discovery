package edu.phystech.servicemesh.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Setter
@Getter
public class Proxy {
    /**
     * Внешний эндпоинт, на котором работает сервис на ноде
     */
    private Endpoint ingressEndpoint;
    private Endpoint monitoringEndpoint;
    private String localAddress;
}
