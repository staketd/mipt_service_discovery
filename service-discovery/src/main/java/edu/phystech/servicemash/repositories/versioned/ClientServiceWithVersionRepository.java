package edu.phystech.servicemash.repositories.versioned;

import edu.phystech.servicemash.ClientServiceVersioned;
import edu.phystech.servicemash.ClientServiceVersionedId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientServiceWithVersionRepository extends MongoRepository<ClientServiceVersioned, ClientServiceVersionedId> {
}
