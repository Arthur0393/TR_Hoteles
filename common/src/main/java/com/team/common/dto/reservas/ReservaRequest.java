package com.team.common.dto.reservas;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Date;

public record ReservaRequest(
        @Schema(
                description = "Identificador unico del huesped",
                example = "1"
        )
        @NotNull(message = "El identificador del huesped es requerido")
        @Positive(message = "El identificador del huesped debe ser mayor a cero")
        Long idHuesped,

        @Schema(
                description = "Identificador unico de la habitacion",
                example = "1"
        )
        @NotNull(message = "El identificador de la habitacion es requerido")
        @Positive(message = "El identificador de la habitacion debe ser mayor a cero")
        Long idHabitacion,

        @Schema(
                description = "Fecha de entrada del huesped",
                example = "19/09/2026"
        )
        @NotNull(message = "La fecha de entrada del huesped es requerida")
        Date fechaEntrada,

        @Schema(
                description = "Fecha de salida del huesped",
                example = "30/09/2026"
        )
        @NotNull(message = "La fecha de salida del huesped es requerida")
        Date fechaSalida
) {}
