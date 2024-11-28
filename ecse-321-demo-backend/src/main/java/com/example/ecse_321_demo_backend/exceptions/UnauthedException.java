package com.example.ecse_321_demo_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthedException extends RuntimeException {

    public UnauthedException(String message) {
        super(message);
    }
}
