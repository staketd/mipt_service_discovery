package edu.phystech.servicemash.response;

public class ResponseWrapper<T> {
    private T response;

    public ResponseWrapper(T response) {
        this.response = response;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    public static <T> ResponseWrapper<T> buildResponse(T response) {
        return new ResponseWrapper<>(response);
    }
}
