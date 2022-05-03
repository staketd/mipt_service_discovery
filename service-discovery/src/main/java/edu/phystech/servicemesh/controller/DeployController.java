package edu.phystech.servicemesh.controller;

import edu.phystech.servicemesh.DeployVersionProcessor;
import edu.phystech.servicemesh.response.ResponseStatus;
import edu.phystech.servicemesh.response.ResponseWrapper;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeployController {
    private final DeployVersionProcessor deployVersionProcessor;

    public DeployController(DeployVersionProcessor deployVersionProcessor) {
        this.deployVersionProcessor = deployVersionProcessor;
    }

    @PutMapping(value = "/service/version/deploy")
    public ResponseWrapper<ResponseStatus> deployNewVersion(
            @RequestParam(value = "service_id") String serviceId,
            @RequestParam(value = "deployed_version") long version
    ) {
        deployVersionProcessor.updateDeployedVersion(serviceId, version);
        return ResponseWrapper.buildResponse(ResponseStatus.Success);
    }
}
