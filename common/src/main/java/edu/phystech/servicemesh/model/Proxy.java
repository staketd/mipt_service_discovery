package edu.phystech.servicemesh.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


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
