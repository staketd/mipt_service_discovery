package edu.phystech.servicemesh;

import edu.phystech.servicemesh.model.NodeLayout;
import edu.phystech.servicemesh.repositories.node.NodeLayoutRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class NodeLayoutDao {
    private final NodeLayoutRepository nodeLayoutRepository;

    public NodeLayoutDao(NodeLayoutRepository nodeLayoutRepository) {
        this.nodeLayoutRepository = nodeLayoutRepository;
    }

    public Map<String, NodeLayout> getLayouts(Collection<String> ids) {
        Map<String, NodeLayout> result = StreamSupport.stream(nodeLayoutRepository.findAllById(ids).spliterator(), false)
                .collect(Collectors.toMap(NodeLayout::getNodeIdentifier, Function.identity()));

        if (!result.keySet().containsAll(ids)) {
            for (String id: ids) {
                if (!result.containsKey(id)) {
                    throw new ObjectNotFoundException(id, "Node");
                }
            }
        }

        return result;
    }

    public void saveNodeLayouts(Collection<NodeLayout> layouts) {
        nodeLayoutRepository.saveAll(layouts);
    }

    public NodeLayout saveNodeLayout(NodeLayout layout) {
        return nodeLayoutRepository.save(layout);
    }

    public List<NodeLayout> getAllLayouts() {
        return nodeLayoutRepository.findAll();
    }

    public NodeLayout getNodeLayoutById(String nodeId) {
        return nodeLayoutRepository.findById(nodeId).orElseThrow(() -> new ObjectNotFoundException(nodeId, "node"));
    }

    public void deleteNode(String nodeId) {
        nodeLayoutRepository.deleteById(nodeId);
    }
}
