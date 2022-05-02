package edu.phystech.servicemesh;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ServiceInstanceId {
    @NotEmpty
    private String id;
    @NotEmpty
    private String nodeId;
}
