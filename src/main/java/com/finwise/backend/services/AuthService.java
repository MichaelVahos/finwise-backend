package com.finwise.backend.services;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public String saludo() {
        return "Hola desde AuthService!";
    }

}