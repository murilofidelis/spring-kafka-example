package br.com.kafka.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AppException extends RuntimeException {

    public AppException(String var) {
        super(var);
    }

    public AppException(String var, Throwable throwable) {
        super(var, throwable);
    }
}
