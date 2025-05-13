package com.finwise.backend.services;

import com.finwise.backend.enums.Rol;
import com.finwise.backend.models.Usuario;
import com.finwise.backend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registrar un nuevo usuario
    public Usuario registrarUsuario(Usuario usuario) {
        // Si el rol no fue especificado, se asigna USER por defecto
        if (usuario.getRol() == null) {
            usuario.setRol(Rol.USER);
        }

        // Cifrar la contraseña antes de guardarla
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        return usuarioRepository.save(usuario);
    }


    // Buscar usuario por username
    public Optional<Usuario> obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    // Buscar usuario por email
    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public boolean verificarPassword(String passwordPlano, String passwordCifrado) {
        return passwordEncoder.matches(passwordPlano, passwordCifrado);
    }

    // Guardar (crear o actualizar) usuario
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }


}