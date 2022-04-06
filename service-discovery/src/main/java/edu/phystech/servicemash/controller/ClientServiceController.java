package edu.phystech.servicemash.controller;

import edu.phystech.servicemash.ClientService;
import edu.phystech.servicemash.ClientServiceProcessor;
import edu.phystech.servicemash.response.ResponseWrapper;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;

import static edu.phystech.servicemash.response.ResponseWrapper.buildResponse;

@RestController
public class ClientServiceController {

    private final ClientServiceProcessor serviceProcessor;

    public ClientServiceController(ClientServiceProcessor serviceProcessor) {
        this.serviceProcessor = serviceProcessor;
    }

    @RequestMapping(value = "/service", method = RequestMethod.POST)
    public ResponseWrapper<ClientService> createService(
            @RequestParam(name = "service_id") String serviceId,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "fqdn") String fqdn
    ) {
        return buildResponse(serviceProcessor.createService(serviceId, name, fqdn));
    }

    @RequestMapping(value = "/service", method = RequestMethod.PUT)
    public ResponseWrapper<ClientService> editServiceName(
            @RequestParam(name = "service_id") String serviceId,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "fqdn") String fqdn
    ) {
        return buildResponse(serviceProcessor.editMeta(serviceId, name, fqdn));
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
    public ResponseWrapper<ClientService> addUsedService(
            @RequestParam("service_id") String serviceId,
            @RequestParam("used_services") String[] services
    ) {
        return buildResponse(serviceProcessor.addUsedService(serviceId, new HashSet<>(Arrays.asList(services))));
    }
}
