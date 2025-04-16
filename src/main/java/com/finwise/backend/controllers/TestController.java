package com.finwise.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:4200")
public class TestController {

    @GetMapping
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Acceso autorizado con token JWT");
    }
}