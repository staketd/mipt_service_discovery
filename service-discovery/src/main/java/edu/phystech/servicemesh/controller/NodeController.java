package edu.phystech.servicemesh.controller;

import edu.phystech.servicemesh.NodeLayoutService;
import edu.phystech.servicemesh.model.NodeLayout;
import edu.phystech.servicemesh.model.request.AddNodeRequest;
import edu.phystech.servicemesh.response.ResponseStatus;
import edu.phystech.servicemesh.response.ResponseWrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
