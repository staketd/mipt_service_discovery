package edu.phystech.servicemesh.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "node_layouts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NodeLayout {
    @Id
    private String nodeIdentifier;
    private Map<String, AddressLayout> ingressAddressesLayout;

    public NodeLayout(String nodeIdentifier, Collection<String> addresses) {
        this.nodeIdentifier = nodeIdentifier;
        ingressAddressesLayout = new HashMap<>();
        addresses.forEach(address -> ingressAddressesLayout.put(address, new AddressLayout()));
    }

    public Endpoint allocateIngressEndpoint() {
        for (var entry: ingressAddressesLayout.entrySet()) {
            if (entry.getValue().canAllocate()) {
                return new Endpoint(entry.getKey(), entry.getValue().allocatePort());
            }
        }
        return null;
    }

    public Endpoint allocateEgressEndpoint() {
        return new Endpoint("127.0.0.1", 25565);
    }

    public void deallocateEndpoint(Endpoint endpoint) {
        ingressAddressesLayout.get(endpoint.getAddress()).deallocatePort(endpoint.getPort());
    }
}
