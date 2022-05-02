package edu.phystech.servicemesh.exception;

import java.util.HashMap;
import java.util.Map;

import edu.phystech.servicemesh.response.ApiError;
import edu.phystech.servicemesh.response.ResponseWrapper;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class DefaultExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseWrapper<Map<String, String>> handleError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseWrapper<>(errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {
            NodeIsNotEmptyException.class,
            ServiceAlreadyExistsException.class,
            ServiceIsNotPresentOnNodeException.class,
            WrongParameterException.class,
            ObjectNotFoundException.class,
            ServiceInstanceNotExistsException.class,
            CommonApiException.class
    })
    @ResponseBody
    public ResponseWrapper<ApiError> handleException(Exception e) {
        return ResponseWrapper.buildResponse(new ApiError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }
}
