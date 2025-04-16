package com.finwise.backend.controllers;

import com.finwise.backend.dto.LoginRequest;
import com.finwise.backend.dto.RegistroRequest;
import com.finwise.backend.services.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.finwise.backend.models.Usuario;
import com.finwise.backend.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200") // para permitir peticiones desde Angular
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        // Buscar el usuario por username
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(request.getUsername())
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(401).body("Usuario no encontrado");
        }

        // Verificar la contraseña
        boolean passwordOk = usuarioService.verificarPassword(request.getPassword(), usuario.getPassword());

        if (!passwordOk) {
            return ResponseEntity.status(401).body("Contraseña incorrecta");
        }

        return ResponseEntity.ok("Inicio de sesión exitoso");
    }

}