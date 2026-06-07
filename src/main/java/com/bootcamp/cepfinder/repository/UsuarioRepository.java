package com.bootcamp.cepfinder.repository;

import com.bootcamp.cepfinder.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório JPA para a entidade {@link Usuario}.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);
}
