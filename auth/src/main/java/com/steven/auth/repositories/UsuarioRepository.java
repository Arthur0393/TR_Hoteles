package com.steven.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.steven.auth.entities.Usuario;
import com.steven.auth.entities.EstadoRegistro;
import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsernameAndEstadoRegistro(String username, EstadoRegistro estadoRegistro);

    Optional<Usuario> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);

    List<Usuario> findAllByEstadoRegistro(EstadoRegistro estadoRegistro);

    boolean existsByUsernameAndEstadoRegistro(String username, EstadoRegistro estadoRegistro);

    boolean existsByUsernameAndEstadoRegistroAndIdNot(String username, EstadoRegistro estadoRegistro, Long id);

    boolean existsByUsername(String username);

}
