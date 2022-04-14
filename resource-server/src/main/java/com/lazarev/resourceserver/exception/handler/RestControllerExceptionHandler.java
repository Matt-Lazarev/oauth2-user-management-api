package com.lazarev.resourceserver.exception.handler;

import com.lazarev.resourceserver.exception.custom.ApplicationUserNotFoundException;
import com.lazarev.resourceserver.exception.custom.RoleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler({ApplicationUserNotFoundException.class, RoleNotFoundException.class})
    public ResponseEntity<?> notFoundExceptionHandler(ApplicationUserNotFoundException exception){
        Map<String, String> errors = new HashMap<>();
        errors.put(HttpStatus.NOT_FOUND.name(), exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errors);
    }
}
