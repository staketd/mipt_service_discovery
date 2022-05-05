package edu.phystech.servicemesh.controller;

import edu.phystech.servicemesh.EnvoyService;
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

    @GetMapping(value = "update-result")
    public ResponseWrapper<EnvoyConfig> getEnvoyConfig(
            @RequestParam(value = "node_id") String envoyNodeId,
            @RequestParam(value = "cluster_id") String envoyClusterId
    ) {
        EnvoyId envoyId = new EnvoyId(envoyClusterId, envoyNodeId);
        return ResponseWrapper.buildResponse(envoyService.getEnvoyConfig(envoyId.getClusterId(), envoyId));
    }
}
