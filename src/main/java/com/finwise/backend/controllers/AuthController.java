package com.finwise.backend.controllers;

import com.finwise.backend.enums.Rol;
import com.finwise.backend.security.JwtUtil;
import com.finwise.backend.dto.LoginRequest;
import com.finwise.backend.dto.LoginResponse;
import com.finwise.backend.dto.RegistroRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import com.finwise.backend.models.Usuario;
import com.finwise.backend.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        // Si el rol viene en el request, se usa. Si no, el service pondrá USER por defecto
        if (request.getRol() != null) {
            usuario.setRol(request.getRol());
        }


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
        String token = jwtUtil.generateToken(usuario);

        // Crear respuesta personalizada
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("usuario", Map.of(
                "id", usuario.getId(),
                "username", usuario.getUsername(),
                "email", usuario.getEmail(),
                "rol", usuario.getRol(),
                "authorities", usuario.getAuthorities()
        ));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorUsername(username);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        return ResponseEntity.ok(Map.of(
                "id", usuario.getId(),
                "username", usuario.getUsername(),
                "email", usuario.getEmail(),
                "rol", usuario.getRol()
        ));
    }






}