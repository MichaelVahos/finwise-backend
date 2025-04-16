package com.finwise.backend.controllers;

import com.finwise.backend.security.JwtUtil;
import com.finwise.backend.dto.LoginRequest;
import com.finwise.backend.dto.LoginResponse;
import com.finwise.backend.dto.RegistroRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import com.finwise.backend.models.Usuario;
import com.finwise.backend.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200") // para permitir peticiones desde Angular
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public AuthController(UsuarioService usuarioService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@RequestBody RegistroRequest request) {
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(request.getPassword());

        Usuario nuevo = usuarioService.registrarUsuario(usuario);
        return ResponseEntity.ok(nuevo);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorUsername(request.getUsername());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(401).body("Contraseña incorrecta");
        }

        // Generar token
        String token = jwtUtil.generateToken(usuario.getUsername());

        return ResponseEntity.ok(new LoginResponse(token));
    }

}