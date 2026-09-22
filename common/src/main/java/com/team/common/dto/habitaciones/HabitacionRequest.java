package com.team.common.dto.habitaciones;

import com.team.common.enums.TipoHabitacion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record HabitacionRequest(

        @Schema(
                description = "Numero de la habitación",
                example = "1"
        )
        @NotNull(message = "El numero de habitación es requerido")
        @Positive(message = "El numero de habitación debe ser mayor que cero")
        Integer numeroHabitacion,

        @Schema(
                description = "Tipo de habitación",
                example = "SENCILLA"
        )
        @NotNull(message = "El tipo de habitacion es requerido")
        TipoHabitacion tipoHabitacion,

        @Schema(
                description = "Precio de la habitación",
                example = "5000.00"
        )
        @NotNull(message = "El precio de la habitación es requerida")
        @DecimalMin(
                value = "0.0",
                inclusive = false,
                message = "El precio debe ser mayor que cero"
        )
        @Digits(
                integer = 8,
                fraction = 2,
                message = "El precio debe tener maximo 8 enteros y 2 decimales"
        )
        BigDecimal precio,


        @Schema(
                description = "Capacidad de la habitación",
                example = "4"
        )
        @NotNull(message = "La capacidad de la habitación es requerida")
        @Min(value = 1, message = "La capacidad deber ser minimo 1")
        Integer capacidad
) {}
