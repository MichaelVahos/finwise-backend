package com.finwise.backend.config;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.FieldError;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String,String> errores = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errores.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> handleAll(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getMessage()));
    }
}