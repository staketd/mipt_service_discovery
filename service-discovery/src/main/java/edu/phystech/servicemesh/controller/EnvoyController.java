package edu.phystech.servicemesh.controller;

import edu.phystech.servicemesh.EnvoyService;
import edu.phystech.servicemesh.model.EnvoyType;
import edu.phystech.servicemesh.model.envoy.EnvoyConfig;
import edu.phystech.servicemesh.model.envoy.EnvoyId;
import edu.phystech.servicemesh.response.ResponseWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnvoyController {
    private final EnvoyService envoyService;

    public EnvoyController(EnvoyService envoyService) {
        this.envoyService = envoyService;
    }

    @GetMapping(value = "/service/envoy")
    public ResponseWrapper<EnvoyConfig> getEnvoyConfig(
            @RequestParam(value = "service_id") String serviceId,
            @RequestParam(value = "envoy_type") EnvoyType envoyType,
            @RequestParam(value = "envoy_node_id") String envoyNodeId,
            @RequestParam(value = "envoy_cluster_id") String envoyClusterId
    ) {
        return ResponseWrapper.buildResponse(envoyService.getEnvoyConfig(serviceId, envoyType, new EnvoyId(envoyClusterId, envoyNodeId)));
    }
}
