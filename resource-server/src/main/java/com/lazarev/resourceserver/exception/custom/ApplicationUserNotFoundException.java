package com.lazarev.resourceserver.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ApplicationUserNotFoundException extends RuntimeException {
    public ApplicationUserNotFoundException(String message) {
        super(message);
    }
}
