package edu.phystech.servicemash.repositories.simple;

import edu.phystech.servicemash.ClientService;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClientServiceRepository extends MongoRepository<ClientService, Long> {
}
