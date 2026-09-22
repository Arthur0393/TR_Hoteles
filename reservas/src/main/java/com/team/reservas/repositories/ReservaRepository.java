package com.team.reservas.repositories;

import com.team.common.enums.EstadoRegistro;
import com.team.common.enums.EstadoReserva;
import com.team.reservas.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Permite listar registros activos sin excluir reservas finalizadas o canceladas.
    List<Reserva> findAllByEstadoRegistro(EstadoRegistro estadoRegistro);

    Optional<Reserva> findByIdReservaAndEstadoRegistro(
            Long idReserva, EstadoRegistro estadoRegistro);

    // El service consultará EN_CURSO antes de permitir eliminar un huésped.
    boolean existsByIdHuespedAndEstadoReservaAndEstadoRegistro(
            Long idHuesped, EstadoReserva estadoReserva, EstadoRegistro estadoRegistro);

    // CONFIRMADA y EN_CURSO identifican las reservas que mantienen ocupada la habitación.
    // Esta consulta por sí sola no evita dos creaciones simultáneas.
    boolean existsByIdHabitacionAndEstadoReservaInAndEstadoRegistro(
            Long idHabitacion, Collection<EstadoReserva> estadosReserva,
            EstadoRegistro estadoRegistro);
}
