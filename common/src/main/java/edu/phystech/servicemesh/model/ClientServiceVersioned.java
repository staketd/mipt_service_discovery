package edu.phystech.servicemesh.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "service_versions")
public class ClientServiceVersioned {
    @MongoId
    private ClientServiceVersionedId id;
    private ClientService clientService;
    public ClientServiceVersioned(ClientService service) {
        this.clientService = service;
        this.id = new ClientServiceVersionedId(clientService.getServiceId(), clientService.getVersion());
    }

    public ClientServiceVersioned() {
    }

    public ClientService getClientService() {
        return clientService;
    }

    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    public ClientServiceVersionedId getId() {
        return id;
    }

    public void setId(ClientServiceVersionedId id) {
        this.id = id;
    }
}
