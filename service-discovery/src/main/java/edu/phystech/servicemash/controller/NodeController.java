package edu.phystech.servicemash.controller;

import edu.phystech.servicemash.NodeService;
import edu.phystech.servicemash.response.ResponseStatus;
import edu.phystech.servicemash.response.ResponseWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class NodeController {
    private final NodeService nodeService;

    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @RequestMapping(value = "/node/discover", method = RequestMethod.POST)
    public ResponseWrapper<ResponseStatus> discoverNode(
            @RequestParam(name = "service_id") String serviceId,
            @RequestParam(name = "address") String address,
            @RequestParam(name = "port") int port
    ) {
        nodeService.discoverNode(serviceId, address, port);
        return ResponseWrapper.buildResponse(ResponseStatus.Success);
    }

    @RequestMapping(value = "/node/delete", method = RequestMethod.DELETE)
    public ResponseWrapper<ResponseStatus> deleteNode(
            @RequestParam(name = "service_id") String serviceId,
            @RequestParam(name = "address") String address,
            @RequestParam(name = "port") int port
    ) {
        nodeService.deregisterNode(serviceId, address, port);
        return ResponseWrapper.buildResponse(ResponseStatus.Success);
    }
}
