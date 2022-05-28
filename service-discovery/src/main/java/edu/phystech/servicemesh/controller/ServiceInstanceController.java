package edu.phystech.servicemesh.controller;

import edu.phystech.servicemesh.InstanceProcessor;
import edu.phystech.servicemesh.model.ClientService;
import edu.phystech.servicemesh.model.request.AddInstancesRequest;
import edu.phystech.servicemesh.model.request.DeleteInstancesRequest;
import edu.phystech.servicemesh.model.request.MoveInstanceRequest;
import edu.phystech.servicemesh.response.ResponseWrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static edu.phystech.servicemesh.response.ResponseWrapper.buildResponse;

@RestController
public class ServiceInstanceController {
    private final InstanceProcessor instanceProcessor;

    public ServiceInstanceController(InstanceProcessor instanceProcessor) {
        this.instanceProcessor = instanceProcessor;
    }

    @PutMapping(value = "/service/instance/move")
    public ResponseWrapper<ClientService> moveInstance(
            @Valid @RequestBody MoveInstanceRequest request
    ) {
        return buildResponse(instanceProcessor.moveInstance(request.getServiceId(), request.getFromServiceInstanceId(), request.getToServiceInstanceId()));
    }

    @PostMapping(value = "/service/instance/add")
    public ResponseWrapper<ClientService> addInstances(
            @Valid @RequestBody AddInstancesRequest request
    ) {
        return buildResponse(instanceProcessor.addInstances(request.getServiceId(), request.getServiceInstanceIds()));
    }

    @DeleteMapping(value = "/service/instance/delete")
    public ResponseWrapper<ClientService> deleteInstances(
            @Valid @RequestBody DeleteInstancesRequest request
    ) {
        return buildResponse(instanceProcessor.deallocateInstances(request.getServiceId(), request.getServiceInstanceIds()));
    }
}
