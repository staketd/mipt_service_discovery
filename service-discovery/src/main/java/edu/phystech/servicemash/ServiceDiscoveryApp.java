package edu.phystech.servicemash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ServiceDiscoveryApp {
    public static void main(String[] args) {
        SpringApplication.run(ServiceDiscoveryApp.class, args);
    }
}
