package edu.phystech.servicemesh.controller;

import edu.phystech.servicemesh.DeployVersionProcessor;
import edu.phystech.servicemesh.model.DeployVersionRequest;
import edu.phystech.servicemesh.response.ResponseStatus;
import edu.phystech.servicemesh.response.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class DeployController {
    private final DeployVersionProcessor deployVersionProcessor;

    public DeployController(DeployVersionProcessor deployVersionProcessor) {
        this.deployVersionProcessor = deployVersionProcessor;
    }

    @PutMapping(value = "/service/version/deploy")
    public ResponseWrapper<ResponseStatus> deployNewVersion(
            @RequestBody DeployVersionRequest request
    ) {
        if (request.getError() != null) {
            log.error(request.getError());
            return ResponseWrapper.buildResponse(ResponseStatus.Error);
        }
        deployVersionProcessor.updateDeployedVersion(request.getServiceId(), request.getVersion());
        return ResponseWrapper.buildResponse(ResponseStatus.Success);
    }
}
