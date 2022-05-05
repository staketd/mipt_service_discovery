package edu.phystech.servicemesh;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.phystech.servicemesh.model.envoy.ChangeEnvoyConfigRequest;
import org.springframework.stereotype.Service;

@Service
public class EventNotifier {
    private final static String CONTROLLER_HOST = "http://localhost:8123";
    private HttpClient httpClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    private int connectTimeoutMs = 100;
    private int requestTimeoutMs = 2000;

    @PostConstruct
    public void afterPropertiesSet() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                .build();
    }

    public void sendNewServiceVersion(ChangeEnvoyConfigRequest envoyRequest) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(CONTROLLER_HOST))
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(envoyRequest)))
                    .timeout(Duration.ofMillis(requestTimeoutMs))
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
