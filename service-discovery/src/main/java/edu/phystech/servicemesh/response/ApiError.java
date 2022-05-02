package edu.phystech.servicemesh.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter
@Setter
public class ApiError {
    private final HttpStatus responseCode;
    private final String message;

    public ApiError(
            HttpStatus responseCode,
            String message
    ) {
        this.responseCode = responseCode;
        this.message = message;
    }
}
