package edu.phystech.servicemesh.controller;

import edu.phystech.servicemesh.ClientServiceProcessor;
import edu.phystech.servicemesh.model.ClientService;
import edu.phystech.servicemesh.model.request.CreateServiceRequest;
import edu.phystech.servicemesh.response.ResponseStatus;
import edu.phystech.servicemesh.response.ResponseWrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static edu.phystech.servicemesh.response.ResponseWrapper.buildResponse;

@RestController
public class ClientServiceController {

    private final ClientServiceProcessor serviceProcessor;

    public ClientServiceController(ClientServiceProcessor serviceProcessor) {
        this.serviceProcessor = serviceProcessor;
    }

    @RequestMapping(value = "/service", method = RequestMethod.POST)
    public ResponseWrapper<ClientService> createService(
            @Valid @RequestBody CreateServiceRequest createServiceRequest
    ) {
        return buildResponse(serviceProcessor.createService(createServiceRequest));
    }

    @DeleteMapping(value = "/service")
    public ResponseWrapper<ResponseStatus> deleteService(
            @RequestParam(value = "service_id") String serviceId
    ) {
        serviceProcessor.deleteService(serviceId);
        return buildResponse(ResponseStatus.Success);
    }

    @PutMapping(value = "/service/move/balancer")
    public ResponseWrapper<ClientService> moveBalancer(
            @RequestParam(value = "service_id") String serviceId,
            @RequestParam(value = "to_node_id") String nodeId
    ) {
        return buildResponse(serviceProcessor.moveBalancer(serviceId, nodeId));
    }

    @RequestMapping(value = "/service/version", method = RequestMethod.GET)
    public ResponseWrapper<ClientService> getServiceVersion(
            @RequestParam("service_id") String serviceId,
            @RequestParam("version") long version
    ) {
        return buildResponse(serviceProcessor.getServiceByServiceIdAndVersion(serviceId, version));
    }

    @RequestMapping(value = "/service", method = RequestMethod.GET)
    public ResponseWrapper<ClientService> getService(
            @RequestParam("service_id") String serviceId
    ) {
        return buildResponse(serviceProcessor.getCurrentServiceVersion(serviceId));
    }

    @RequestMapping(value = "/service/usedservices/edit", method = RequestMethod.PUT)
    public ResponseWrapper<ClientService> changeUsedService(
            @RequestParam("service_id") String serviceId,
            @RequestParam("used_services") String[] services
    ) {
        return buildResponse(serviceProcessor.changeUsedService(serviceId, new HashSet<>(Arrays.asList(services))));
    }

    @RequestMapping(value = "/services", method = RequestMethod.GET)
    public ResponseWrapper<List<ClientService>> clientServices() {
        return buildResponse(serviceProcessor.getServices());
    }
}
