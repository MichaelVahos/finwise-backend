package com.finwise.backend.services;

import com.finwise.backend.dto.ActualizarTransaccionRequest;
import com.finwise.backend.dto.TransaccionRequest;
import com.finwise.backend.models.Transaccion;
import com.finwise.backend.models.Usuario;
import com.finwise.backend.repositories.TransaccionRepository;
import com.finwise.backend.repositories.UsuarioRepository;
import com.finwise.backend.security.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    public TransaccionService(
            TransaccionRepository transaccionRepository,
            UsuarioRepository usuarioRepository,
            JwtUtil jwtUtil
    ) {
        this.transaccionRepository = transaccionRepository;
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
    }

    public Transaccion crearTransaccion(TransaccionRequest dto, String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Transaccion t = new Transaccion();
        t.setFecha(dto.getFecha());
        t.setDescripcion(dto.getDescripcion());
        t.setMonto(dto.getMonto());
        t.setUsuario(user);
        return transaccionRepository.save(t);
    }

    public List<Transaccion> obtenerTransaccionesDelUsuario(String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return transaccionRepository.findAllByUsuario(user);
    }

    /**
     * Devuelve true si la transacción existía y pertenecía al usuario, y se actualizó.
     */
    public boolean actualizarTransaccion(Long id, ActualizarTransaccionRequest dto, String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Optional<Transaccion> opt = transaccionRepository.findById(id);
        if (opt.isEmpty()) return false;

        Transaccion t = opt.get();
        if (!t.getUsuario().getId().equals(user.getId())) return false;

        t.setFecha(dto.getFecha());
        t.setDescripcion(dto.getDescripcion());
        t.setMonto(dto.getMonto());
        transaccionRepository.save(t);
        return true;
    }

    /**
     * Devuelve true si la transacción existía y pertenecía al usuario, y se eliminó.
     */
    public boolean eliminarTransaccion(Long id, String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Optional<Transaccion> opt = transaccionRepository.findById(id);
        if (opt.isEmpty()) return false;

        Transaccion t = opt.get();
        if (!t.getUsuario().getId().equals(user.getId())) return false;

        transaccionRepository.delete(t);
        return true;
    }

    public List<Transaccion> listarPorRango(
            String username,
            LocalDate desde,
            LocalDate hasta
    ) {
        Usuario u = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return transaccionRepository.findAllByUsuarioAndFechaBetween(u, desde, hasta);
    }
}