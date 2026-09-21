package com.team.common.dto.habitaciones;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record HabitacionRequest(

        @Schema(
                description = "Numero de la habitacion",
                example = "1"
        )
        @NotNull(message = "El numero de habitacion es requerido")
        Number numeroHabitacion,

        @Schema(
                description = "Tipo de habitacion",
                example = "SENCILLA"
        )
        @NotBlank(message = "El tipo de la habitacion es requerida")
        @Size(min = 1, max = 50, message = "El tipo de habitacion debe de tener entre 1 y 50 caracteres")
        String tipo,

        @Schema(
                description = "Precio de la habitacion",
                example = "5000.00"
        )
        @NotNull(message = "El precio de la habitacion es requerida")
        @Size(min = 1, max = 99999)
        BigDecimal precios,


        @Schema(
                description = "Capacidad de la habitacion",
                example = "4"
        )
        @NotNull(message = "La capacidad de la habitacion es requerida")
        Number capacidad
) {}
