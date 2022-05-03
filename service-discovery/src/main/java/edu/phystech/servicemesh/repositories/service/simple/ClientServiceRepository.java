package edu.phystech.servicemesh.repositories.service.simple;

import edu.phystech.servicemesh.model.ClientService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ClientServiceRepository extends MongoRepository<ClientService, String> {

    default ClientService getById(String serviceId) {
        return findById(serviceId).orElseThrow(() -> new ObjectNotFoundException(serviceId, "service"));
    }
}
