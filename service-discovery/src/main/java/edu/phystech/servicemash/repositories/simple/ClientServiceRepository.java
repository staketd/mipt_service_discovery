package edu.phystech.servicemash.repositories.simple;

import edu.phystech.servicemash.ClientService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClientServiceRepository extends MongoRepository<ClientService, String> {
    default ClientService getById(String serviceId) {
        return findById(serviceId).orElseThrow(() -> new ObjectNotFoundException(serviceId, "service"));
    }
}
