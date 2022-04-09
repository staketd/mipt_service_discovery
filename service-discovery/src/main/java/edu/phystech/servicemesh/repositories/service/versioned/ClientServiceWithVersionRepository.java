package edu.phystech.servicemesh.repositories.service.versioned;

import edu.phystech.servicemesh.model.ClientServiceVersioned;
import edu.phystech.servicemesh.model.ClientServiceVersionedId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientServiceWithVersionRepository extends MongoRepository<ClientServiceVersioned, ClientServiceVersionedId> {
}
