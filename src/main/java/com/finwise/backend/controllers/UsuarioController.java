package com.finwise.backend.controllers;

import com.finwise.backend.models.Usuario;
import com.finwise.backend.security.JwtUtil;
import com.finwise.backend.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioService usuarioService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
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
    @PutMapping("/password")
    public ResponseEntity<?> cambiarPassword(@RequestBody Map<String, String> payload, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorUsername(username);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        String currentPassword = payload.get("currentPassword");
        String newPassword = payload.get("newPassword");

        if (!passwordEncoder.matches(currentPassword, usuario.getPassword())) {
            return ResponseEntity.status(403).body("La contraseña actual es incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioService.guardar(usuario);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));

    }


}
