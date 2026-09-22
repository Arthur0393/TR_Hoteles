package com.team.common.dto.habitaciones;

import com.team.common.enums.EstadoHabitacion;
import com.team.common.enums.EstadoRegistro;
import com.team.common.enums.TipoHabitacion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record HabitacionResponse(
        @Schema(
                description = "Identificador unico de la habitacion",
                example = "1"
        )
        Long idHabitacion,
        @Schema(
                description = "Numero de la habitacion",
                example = "1"
        )
        Integer numeroHabitacion,
        @Schema(
                description = "Tipo de habitacion",
                example = "SENCILLA"
        )
        TipoHabitacion tipoHabitacion,
        @Schema(
                description = "Precio de la habitacion",
                example = "5000.00"
        )
        BigDecimal precio,
        @Schema(
                description = "Capacidad de la habitacion",
                example = "4"
        )
        Integer capacidad,
        @Schema(
                description = "Estado de la habitacion",
                example = "OCUPADA"
        )
        EstadoHabitacion estadoHabitacion,
        @Schema(
                description = "Estado del registro de la Habitacion",
                example = "ACTIVO"
        )
        EstadoRegistro estadoRegistro
) {}
