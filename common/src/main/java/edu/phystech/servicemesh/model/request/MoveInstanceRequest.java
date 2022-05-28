package edu.phystech.servicemesh.model.request;

import edu.phystech.servicemesh.model.ServiceInstanceId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class MoveInstanceRequest {
    @NotNull
    private String serviceId;

    @NotNull
    @Valid
    private ServiceInstanceId fromServiceInstanceId;

    @NotNull
    @Valid
    private ServiceInstanceId toServiceInstanceId;
}
