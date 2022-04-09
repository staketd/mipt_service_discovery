package edu.phystech.servicemesh.repositories.node;

import edu.phystech.servicemesh.model.NodeLayout;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NodeLayoutRepository extends MongoRepository<NodeLayout, String> {
}
