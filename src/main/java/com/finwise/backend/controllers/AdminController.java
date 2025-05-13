package com.finwise.backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    @GetMapping("/saludo")
    @PreAuthorize("hasRole('ADMIN')")
    public String saludoAdmin() {
        return "¡Hola, ADMIN! Este endpoint está protegido por rol.";
    }
}
