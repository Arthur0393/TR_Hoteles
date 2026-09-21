package com.team.common.enums;


import com.team.common.exceptions.RecursoNoEncontradoException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public enum EstadoHabitacion {

    DISPONIBLE(1L, "Habitacion disponible para ser usada por el huesped"),
    OCUPADA(2L, "Habitacion ocupada por un huesped"),
    LIMPIEZA(3L,"Habitacion en estado de limpieza"),
    MANTENIMIENTO(4L, "Habitacion en estado de mantenimiento");

    private final Long codigo;

    private final String descripcion;

    public static EstadoHabitacion obtenerEstadoPorCodigo(Long codigo) {
        for(EstadoHabitacion estadoHabitacion : values()) {
            if(Objects.equals(estadoHabitacion.getCodigo(), codigo)) {
                return estadoHabitacion;
            }
        }
        throw new RecursoNoEncontradoException("Codigo del estado de la habitacion no valido: " + codigo);
    }
}
