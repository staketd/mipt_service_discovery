package edu.phystech.servicemesh;

import edu.phystech.servicemesh.model.NodeLayout;
import edu.phystech.servicemesh.repositories.node.NodeLayoutRepository;
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

    public Map<String, NodeLayout> getExistingLayouts(Collection<String> ids) {
        return StreamSupport.stream(nodeLayoutRepository.findAllById(ids).spliterator(), false)
                .collect(Collectors.toMap(NodeLayout::getNodeIdentifier, Function.identity()));
    }

    public void saveNodeLayouts(Collection<NodeLayout> layouts) {
        nodeLayoutRepository.saveAll(layouts);
    }

    public List<NodeLayout> getAllLayouts() {
        return nodeLayoutRepository.findAll();
    }
}
