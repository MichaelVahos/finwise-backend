package com.finwise.backend.repositories;


import com.finwise.backend.models.Transaccion;
import com.finwise.backend.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findAllByUsuario(Usuario usuario);
    List<Transaccion> findAllByUsuarioAndFechaBetween(
            Usuario usuario,
            LocalDate desde,
            LocalDate hasta
    );
}