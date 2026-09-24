package com.team.common.enums;


import com.team.common.exceptions.RecursoNoEncontradoException;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

@Getter
public enum EstadoReserva {

    CONFIRMADA(1L, "Reserva creada", true, false) {
        @Override
        public Set<EstadoReserva> puedeCambiar() {
            return EnumSet.of(EN_CURSO, CANCELADA);
        }
    },

    EN_CURSO(2L, "Check-in realizado", true, false) {
        @Override
        public Set<EstadoReserva> puedeCambiar() {
            return EnumSet.of(FINALIZADA);
        }
    },

    FINALIZADA(3L, "Check-out realizado", false, true) {
        @Override
        public Set<EstadoReserva> puedeCambiar() {
            return Set.of();
        }
    },

    CANCELADA(4L, "Reserva cancelada", false, true) {
        @Override
        public Set<EstadoReserva> puedeCambiar() {
            return Set.of();
        }
    };

    private final Long codigo;

    private final String descripcion;


    private final boolean actualizable;


    private final boolean eliminable;

    EstadoReserva(Long codigo, String descripcion, boolean actualizable, boolean eliminable) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.actualizable = actualizable;
        this.eliminable = eliminable;
    }


    public boolean esSoloFechaSalida() {
        return this == EN_CURSO;
    }

    public abstract Set<EstadoReserva> puedeCambiar();

    public boolean puedeCambiarA(EstadoReserva nuevoEstado) {
        return puedeCambiar().contains(nuevoEstado);
    }

    public static EstadoReserva obtenerEstadoPorCodigo(Long codigo) {
        for (EstadoReserva estadoReserva : values()) {
            if (Objects.equals(estadoReserva.getCodigo(), codigo)) {
                return estadoReserva;
            }
        }
        throw new RecursoNoEncontradoException("Codigo de reserva no valido: " + codigo);
    }
}
