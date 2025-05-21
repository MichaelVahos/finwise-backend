package com.finwise.backend.controllers;

import com.finwise.backend.dto.UsuarioResumenDTO;
import com.finwise.backend.enums.Rol;
import com.finwise.backend.models.Usuario;
import com.finwise.backend.repositories.UsuarioRepository;
import com.finwise.backend.security.JwtUtil;
import com.finwise.backend.services.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioService usuarioService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/usuarios")
    public ResponseEntity<?> listarUsuarios(HttpServletRequest request) {
        String token = extraerToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        Optional<Usuario> adminOpt = usuarioRepository.findByUsername(username);
        if (adminOpt.isEmpty() || adminOpt.get().getRol() != Rol.ADMIN) {
            return ResponseEntity.status(403).body("Acceso denegado");
        }

        List<UsuarioResumenDTO> lista = usuarioRepository.findAll().stream()
                .map(u -> new UsuarioResumenDTO(u.getId(), u.getUsername(), u.getEmail(), u.getRol()))
                .toList();

        return ResponseEntity.ok(lista);
    }

    /** Extrae “Bearer xxxxx” → “xxxxx” */
    private String extraerToken(HttpServletRequest request) {
        String h = request.getHeader("Authorization");
        return (h != null && h.startsWith("Bearer ")) ? h.substring(7) : null;
    }



}
