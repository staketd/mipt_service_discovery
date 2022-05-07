package edu.phystech.servicemesh.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DeployVersionRequest {
    private String serviceId;
    private long version;
    private String error;
}
