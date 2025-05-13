package com.finwise.backend.repositories;

import com.finwise.backend.models.Sugerencia;
import com.finwise.backend.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SugerenciaRepository extends JpaRepository<Sugerencia, Long> {
    List<Sugerencia> findAllByUsuarioOrderByFechaGeneracionDesc(Usuario usuario);
}
