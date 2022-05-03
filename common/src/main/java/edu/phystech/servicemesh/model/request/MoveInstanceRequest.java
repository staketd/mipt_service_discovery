package edu.phystech.servicemesh.model.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import edu.phystech.servicemesh.ServiceInstanceId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
