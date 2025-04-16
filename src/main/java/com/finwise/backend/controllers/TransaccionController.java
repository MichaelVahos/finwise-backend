package com.finwise.backend.controllers;

import com.finwise.backend.dto.ActualizarTransaccionRequest;
import com.finwise.backend.dto.TransaccionRequest;
import com.finwise.backend.models.Transaccion;
import com.finwise.backend.models.Usuario;
import com.finwise.backend.services.TransaccionService;
import com.finwise.backend.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
@CrossOrigin(origins = "http://localhost:4200")
public class TransaccionController {

    private final TransaccionService transaccionService;
    private final JwtUtil jwtUtil;

    public TransaccionController(TransaccionService transaccionService, JwtUtil jwtUtil) {
        this.transaccionService = transaccionService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<?> crearTransaccion(
            @RequestBody TransaccionRequest dto,
            HttpServletRequest request
    ) {
        String token = extraerToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o ausente");
        }

        Transaccion creada = transaccionService.crearTransaccion(dto, token);
        return ResponseEntity.ok(creada);
    }

    @GetMapping
    public ResponseEntity<?> listarTransacciones(HttpServletRequest request) {
        String token = extraerToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o ausente");
        }

        List<Transaccion> todas = transaccionService.obtenerTransaccionesDelUsuario(token);
        return ResponseEntity.ok(todas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTransaccion(
            @PathVariable Long id,
            @RequestBody ActualizarTransaccionRequest dto,
            HttpServletRequest request
    ) {
        String token = extraerToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o ausente");
        }

        boolean ok = transaccionService.actualizarTransaccion(id, dto, token);
        if (!ok) {
            return ResponseEntity.status(403).body("No tienes permiso o no existe esa transacción");
        }
        return ResponseEntity.ok("Transacción actualizada correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTransaccion(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        String token = extraerToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o ausente");
        }

        boolean ok = transaccionService.eliminarTransaccion(id, token);
        if (!ok) {
            return ResponseEntity.status(403).body("No tienes permiso o no existe esa transacción");
        }
        return ResponseEntity.noContent().build();
    }

    private String extraerToken(HttpServletRequest request) {
        String h = request.getHeader("Authorization");
        return (h != null && h.startsWith("Bearer ")) ? h.substring(7) : null;
    }
}