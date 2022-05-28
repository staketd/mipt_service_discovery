package edu.phystech.servicemesh.model.request;

import edu.phystech.servicemesh.model.ServiceInstanceId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

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
