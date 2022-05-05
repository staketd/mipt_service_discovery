package edu.phystech.servicemesh.model.request;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import edu.phystech.servicemesh.model.AddressPortLayout;
import edu.phystech.servicemesh.model.ServiceInstanceId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class CreateServiceRequest {
    @NotNull
    private String serviceId;
    @NotNull
    private String serviceName;

    @Min(value = AddressPortLayout.MIN_PORT)
    @Max(value = (1 << 16) - 1)
    private int port;

    @NotNull
    @Valid
    private String serviceIngressNodeId;

    @NotNull
    private List<@Valid ServiceInstanceId> serviceInstanceIds;

    @NotNull
    private Set<String> usedServices;
}
