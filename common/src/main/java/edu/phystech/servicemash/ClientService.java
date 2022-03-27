package edu.phystech.servicemash;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;

@Document(collection = "services")
public class ClientService {
    @Id
    private long serviceId;

    private String name;
    private String fqdn;

    protected long version;

//    private List<ServiceLocation> serviceLocations;

    public ClientService() {
    }

    public ClientService(
            long serviceId,
            String name,
            String fqdn
    ) {
        this.serviceId = serviceId;
        this.name = name;
        this.fqdn = fqdn;
        this.version = 0;
    }

    public ClientService(ClientService service) {
        this.serviceId = service.serviceId;
        name = service.name;
        fqdn = service.fqdn;
        version = service.version;
    }

//    protected ClientService(
//            long serviceId,
//            String name,
//            String fqdn/*,
//            List<ServiceLocation> serviceLocations*/
//    ) {
////        this.serviceLocations = serviceLocations;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFqdn() {
        return fqdn;
    }

    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }

//    public List<ServiceLocation> getServiceLocations() {
//        return serviceLocations;
//    }
//
//    public void setServiceLocations(List<ServiceLocation> serviceLocations) {
//        this.serviceLocations = serviceLocations;
//    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }
}
