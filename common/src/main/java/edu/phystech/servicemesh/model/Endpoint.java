package edu.phystech.servicemesh.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Endpoint {
    private String address;
    private int port;

    @Override
    public String toString() {
        return address + ":" + port;
    }
}
