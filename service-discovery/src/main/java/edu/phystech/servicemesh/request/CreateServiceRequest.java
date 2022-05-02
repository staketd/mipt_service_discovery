package edu.phystech.servicemesh.request;

import edu.phystech.servicemesh.model.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class CreateServiceRequest {
    @NotNull
    private String serviceId;
    @NotNull
    private String serviceName;

    @NotNull
    @Valid
    private Node serviceIngressNode;

    @NotNull
    private List<@Valid Node> instanceNodes;

    @NotNull
    private Set<String> usedServices;
}
