package edu.phystech.servicemesh.model.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import edu.phystech.servicemesh.model.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddNodeRequest {
    @NotNull
    @Valid
    private Node node;
}
