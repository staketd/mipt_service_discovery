package edu.phystech.servicemesh;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.phystech.servicemesh.model.envoy.ChangeEnvoyConfigRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
@Slf4j
public class EventNotifier {
    private final static String CONTROLLER_HOST = "http://localhost:8079/controller/add-config";
    private HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private int connectTimeoutMs = 100;
    private int requestTimeoutMs = 2000;

    public EventNotifier(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .build();
            log.info(objectMapper.writeValueAsString(envoyRequest));
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
