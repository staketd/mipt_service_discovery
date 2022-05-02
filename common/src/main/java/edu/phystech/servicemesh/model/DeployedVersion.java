package edu.phystech.servicemesh.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "deployed_version")
@Getter
@Setter
@NoArgsConstructor
public class DeployedVersion {
    @MongoId
    private String serviceId;

    private long maxDeployedVersion;
}
