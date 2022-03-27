package edu.phystech.servicemash.controller;

import edu.phystech.servicemash.ClientService;
import edu.phystech.servicemash.ClientServiceProcessor;
import edu.phystech.servicemash.response.ResponseWrapper;
import org.springframework.web.bind.annotation.*;

import static edu.phystech.servicemash.response.ResponseWrapper.buildResponse;

@RestController
public class ClientServiceController {

    private final ClientServiceProcessor serviceProcessor;

    public ClientServiceController(ClientServiceProcessor serviceProcessor) {
        this.serviceProcessor = serviceProcessor;
    }

    @RequestMapping(value = "/service", method = RequestMethod.POST)
    public ResponseWrapper<ClientService> createService(
            @RequestParam(name = "id") long serviceId,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "fqdn") String fqdn
    ) {
        return buildResponse(serviceProcessor.createService(serviceId, name, fqdn));
    }

    @RequestMapping(value = "/service", method = RequestMethod.PUT)
    public ResponseWrapper<ClientService> editServiceName(
            @RequestParam(name = "id") long serviceId,
            @RequestParam(name = "name") String name
    ) {
        return buildResponse(serviceProcessor.editName(serviceId, name));
    }

    @RequestMapping(value = "/service", method = RequestMethod.GET)
    public ResponseWrapper<ClientService> getService(
            @RequestParam("id") long serviceId,
            @RequestParam("version") long version
    ) {
        return buildResponse(serviceProcessor.findByServiceIdAndVersion(serviceId, version));
    }
}
