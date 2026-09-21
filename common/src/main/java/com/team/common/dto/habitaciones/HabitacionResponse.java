package com.team.common.dto.habitaciones;

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
        Number numero,
        @Schema(
                description = "Tipo de habitacion",
                example = "SENCILLA"
        )
        String tipo,
        @Schema(
                description = "Precio de la habitacion",
                example = "5000.00"
        )
        BigDecimal precios,
        @Schema(
                description = "Capacidad de la habitacion",
                example = "4"
        )
        Number capacidad,
        @Schema(
                description = "Estado de la habitacion",
                example = "OCUPADA"
        )
        String estadoHabitacion,
        @Schema(
                description = "Estado del registro de la Habitacion",
                example = "ACTIVO"
        )
        String estadoRegistro
) {
}
