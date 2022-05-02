package edu.phystech.servicemesh.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.validation.Valid;

import edu.phystech.servicemesh.ClientServiceProcessor;
import edu.phystech.servicemesh.model.ClientService;
import edu.phystech.servicemesh.request.CreateServiceRequest;
import edu.phystech.servicemesh.response.ResponseWrapper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
