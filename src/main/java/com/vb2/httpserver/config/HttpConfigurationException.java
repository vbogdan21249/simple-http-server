package com.vb2.httpserver.config;


public class HttpConfigurationException extends RuntimeException {
    public HttpConfigurationException(String message) {
        super(message);
    }
    public HttpConfigurationException(Throwable cause) {
        super(cause);
    }

    public HttpConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
