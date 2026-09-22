package com.team.common.enums;


import com.team.common.exceptions.RecursoNoEncontradoException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public enum EstadoReserva {

    CONFIRMADA(1L, "Reserva creada"),
    EN_CURSO(2L, "Check-in realizado"),
    FINALIZADA(3L, "Check-out realizado"),
    CANCELADA(4L, "Reserva cancelada");

    private final Long codigo;

    private final String descripcion;

    public static EstadoReserva obtenerEstadoPorCodigo(Long codigo) {
        for (EstadoReserva estadoReserva : values()) {
            if (Objects.equals(estadoReserva.getCodigo(), codigo)) {
                return estadoReserva;
            }
        }
        throw new RecursoNoEncontradoException("Codigo del estado de la reserva no valido: " + codigo);
    }
}
