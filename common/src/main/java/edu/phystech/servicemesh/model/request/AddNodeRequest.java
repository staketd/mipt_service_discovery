package edu.phystech.servicemesh.model.request;

import edu.phystech.servicemesh.model.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddNodeRequest {
    @NotNull
    @Valid
    private Node node;
}
