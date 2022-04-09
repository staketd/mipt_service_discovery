package edu.phystech.servicemesh.model;

import java.io.Serializable;
import java.util.Objects;


public class ClientServiceVersionedId implements Serializable {
    private String serviceId;
    private long version;

    public ClientServiceVersionedId(String serviceId, long version) {
        this.serviceId = serviceId;
        this.version = version;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "ClientServiceWithVersionId{" +
                "serviceId=" + serviceId +
                ", version=" + version +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientServiceVersionedId that = (ClientServiceVersionedId) o;
        return Objects.equals(serviceId, that.serviceId) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId, version);
    }
}
