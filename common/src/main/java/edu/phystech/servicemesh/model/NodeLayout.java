package edu.phystech.servicemesh.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "node_layouts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NodeLayout {
    @Id
    private String nodeIdentifier;
    private Map<String, AddressPortLayout> ingressAddressesLayout;
    private LocalAddressLayout localAddressLayout;

    public NodeLayout(String nodeIdentifier, Collection<String> addresses) {
        this.nodeIdentifier = nodeIdentifier;
        ingressAddressesLayout = new HashMap<>();
        addresses.forEach(address -> ingressAddressesLayout.put(address, new AddressPortLayout()));
        localAddressLayout = new LocalAddressLayout();
    }

    public Endpoint allocateIngressEndpoint() {
        for (var entry: ingressAddressesLayout.entrySet()) {
            if (entry.getValue().canAllocate()) {
                return new Endpoint(entry.getKey(), entry.getValue().allocatePort());
            }
        }
        return null;
    }

    public void deallocateIngressEndpoint(Endpoint endpoint) {
        ingressAddressesLayout.get(endpoint.getAddress()).deallocatePort(endpoint.getPort());
    }

    public String allocateLocalAddress() {
        return localAddressLayout.allocateAddress();
    }

    public void deallocateLocalAddress(String address) {
        localAddressLayout.deallocateAddress(address);
    }

    public boolean isEmpty() {
        return localAddressLayout.isEmpty() && ingressAddressesLayout.values()
                .stream().allMatch(IntResourceAllocator::isEmpty);
    }
}
