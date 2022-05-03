package edu.phystech.servicemesh.controller;

import java.util.List;

import javax.validation.Valid;

import edu.phystech.servicemesh.NodeLayoutService;
import edu.phystech.servicemesh.model.NodeLayout;
import edu.phystech.servicemesh.model.request.AddNodeRequest;
import edu.phystech.servicemesh.response.ResponseStatus;
import edu.phystech.servicemesh.response.ResponseWrapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NodeController {
    private final NodeLayoutService nodeLayoutService;

    public NodeController(NodeLayoutService nodeLayoutService) {
        this.nodeLayoutService = nodeLayoutService;
    }

    @PostMapping(value = "/node")
    public ResponseWrapper<ResponseStatus> discoverNode(
            @Valid @RequestBody AddNodeRequest request
    ) {
        nodeLayoutService.addNode(request.getNode());
        return ResponseWrapper.buildResponse(ResponseStatus.Success);
    }

    @DeleteMapping(value = "/node")
    public ResponseWrapper<ResponseStatus> deleteNode(
            @RequestParam(value = "node_id") String nodeId
    ) {
        nodeLayoutService.deleteNode(nodeId);
        return ResponseWrapper.buildResponse(ResponseStatus.Success);
    }

    @RequestMapping(value = "/node/all", method = RequestMethod.GET)
    public ResponseWrapper<List<NodeLayout>> nodeLayouts() {
        return ResponseWrapper.buildResponse(nodeLayoutService.getAllLayouts());
    }
}
