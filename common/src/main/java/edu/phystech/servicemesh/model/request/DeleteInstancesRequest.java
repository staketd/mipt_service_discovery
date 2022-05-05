package edu.phystech.servicemesh.model.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import edu.phystech.servicemesh.model.ServiceInstanceId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DeleteInstancesRequest {
    @NotNull
    private String serviceId;

    @NotEmpty
    private List<@Valid  ServiceInstanceId> serviceInstanceIds;
}
