package edu.phystech.servicemash;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "services")
public class ClientService {
    @Id
    private String serviceId;

    private String name;
    private String fqdn;

    private long version;

    private ProxyNode proxyNode;
    private Set<Node> instances = new HashSet<>();

    private Set<String> usedServices = new HashSet<>();

    public ClientService() {
    }

    public ClientService(
            String serviceId,
            String name,
            String fqdn
    ) {
        this.serviceId = serviceId;
        this.name = name;
        this.fqdn = fqdn;
        this.version = 0;
    }

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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Set<String> getUsedServices() {
        return usedServices;
    }

    public void setUsedServices(Set<String> usedServices) {
        this.usedServices = usedServices;
    }

    public Set<Node> getInstances() {
        return instances;
    }

    public void setInstances(Set<Node> instances) {
        this.instances = instances;
    }

    public ProxyNode getProxyNode() {
        return proxyNode;
    }

    public void setProxyNode(ProxyNode proxyNode) {
        this.proxyNode = proxyNode;
    }
}
