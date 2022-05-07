package edu.phystech.servicemesh.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.phystech.servicemesh.EnvoyService;
import edu.phystech.servicemesh.model.envoy.EnvoyConfig;
import edu.phystech.servicemesh.model.envoy.EnvoyId;
import edu.phystech.servicemesh.response.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class EnvoyController {
    private final EnvoyService envoyService;
    private final ObjectMapper objectMapper;

    public EnvoyController(EnvoyService envoyService,
                           ObjectMapper objectMapper) {
        this.envoyService = envoyService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "update-result")
    public ResponseWrapper<EnvoyConfig> getEnvoyConfig(
            @RequestParam(value = "node_id") String envoyNodeId,
            @RequestParam(value = "cluster_id") String envoyClusterId
    ) throws JsonProcessingException {
        EnvoyId envoyId = new EnvoyId(envoyClusterId, envoyNodeId);
        EnvoyConfig result = envoyService.getEnvoyConfig(envoyId.getClusterId(), envoyId);
        log.info(objectMapper.writeValueAsString(result));
        return ResponseWrapper.buildResponse(result);
    }
}
