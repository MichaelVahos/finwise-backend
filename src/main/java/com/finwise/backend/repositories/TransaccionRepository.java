package com.finwise.backend.repositories;


import com.finwise.backend.models.Transaccion;
import com.finwise.backend.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findByUsuario(Usuario usuario);
    List<Transaccion> findAllByUsuario(Usuario usuario);
}