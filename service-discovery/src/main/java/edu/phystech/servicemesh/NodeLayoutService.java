package edu.phystech.servicemesh;

import edu.phystech.servicemesh.exception.NodeIsNotEmptyException;
import edu.phystech.servicemesh.model.Node;
import edu.phystech.servicemesh.model.NodeLayout;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NodeLayoutService {

    private final NodeLayoutDao nodeLayoutDao;

    public NodeLayoutService(
            NodeLayoutDao nodeLayoutDao
    ) {
        this.nodeLayoutDao = nodeLayoutDao;
    }

    public List<NodeLayout> getAllLayouts() {
        return nodeLayoutDao.getAllLayouts();
    }

    public void addNode(Node node) {
        nodeLayoutDao.saveNodeLayout(new NodeLayout(node.getNodeIdentifier(), node.getAvailableAddresses()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteNode(String nodeId) {
        NodeLayout layout = nodeLayoutDao.getNodeLayoutById(nodeId);
        if (!layout.isEmpty()) {
            throw new NodeIsNotEmptyException(nodeId);
        }
        nodeLayoutDao.deleteNode(nodeId);
    }
}
