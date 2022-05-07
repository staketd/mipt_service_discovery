package edu.phystech.servicemesh.model.envoy;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.phystech.servicemesh.model.EnvoyType;
import edu.phystech.servicemesh.model.ServiceInstanceId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnvoyId implements Serializable {
    public static final String INSTANCE_PREFIX = "sidecar-";
    public static final String BALANCER_PREFIX = "balancer-";
    private String clusterId; // serviceId
    private String nodeId; // (sidecar-nodeId-serviceInstanceId|balancer-nodeId)
    @JsonIgnore
    private EnvoyType envoyType;

    public static EnvoyId getBalancerId(String serviceId, String nodeId) {
        return new EnvoyId(serviceId, BALANCER_PREFIX + nodeId, EnvoyType.BALANCER);
    }

    public static EnvoyId getInstanceId(String serviceId, String nodeId, String serviceInstanceId) {
        return new EnvoyId(serviceId, INSTANCE_PREFIX + nodeId + "-" + serviceInstanceId, EnvoyType.INSTANCE);
    }

    public EnvoyId(String clusterId, String nodeId) {
        this.envoyType = EnvoyType.BALANCER;
        this.clusterId = clusterId;
        this.nodeId = nodeId;
        if (nodeId.startsWith(INSTANCE_PREFIX)) {
            this.envoyType = EnvoyType.INSTANCE;
        }
    }

    public ServiceInstanceId getServiceInstanceId() {
        if (envoyType == EnvoyType.BALANCER) {
            return null;
        }
        String[] values = nodeId.split("-");
        return new ServiceInstanceId(values[2], values[1]);
    }
}
