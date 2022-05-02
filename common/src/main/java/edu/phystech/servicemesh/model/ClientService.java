package edu.phystech.servicemesh.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "services")
@Getter
@Setter
public class ClientService {
    @Id
    private String serviceId;

    private String name;

    private long version;

    private ServiceIngressProxy serviceIngressProxy;
    private List<ServiceInstance> instances;

    private Set<String> usedByServices = new HashSet<>();
    private Set<String> usedServices;

    public ClientService(String serviceId, String name, Set<String> usedServices) {
        this.serviceId = serviceId;
        this.name = name;
        this.version = 0;
        this.usedServices = usedServices;
    }
}
