package com.team.huespedes.repository;

import com.team.common.enums.Documentacion;
import com.team.common.enums.EstadoRegistro;
import com.team.huespedes.entities.Huesped;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HuespedRepository extends JpaRepository<Huesped,Long> {

    List<Huesped> findAllByEstadoRegistro(EstadoRegistro estadoRegistro);

    Optional<Huesped> findByIdHuespedAndEstadoRegistro(Long idHuesped, EstadoRegistro estadoRegistro);

    Optional<Huesped> findByEmailIgnoreCaseAndEstadoRegistro(String email, EstadoRegistro estadoRegistro);

    Optional<Huesped> findByTelefonoAndEstadoRegistro(String telefono, EstadoRegistro estadoRegistro);

    Optional<Huesped> findByDocumentoAndNumDocumentoAndEstadoRegistro(Documentacion documento, String numDocumento, EstadoRegistro estadoRegistro);
}
