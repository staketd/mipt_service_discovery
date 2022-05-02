package edu.phystech.servicemesh.model;

import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Node {
    @NotNull
    private String nodeIdentifier;
    @NotEmpty
    private Set<String> availableAddresses;
}
