package com.team.common.dto.reservas;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;

public record ReservaRequest(
        @Schema(
                description = "Fecha de entrada del huesped",
                example = "19/09/2026"
        )
        @NotBlank(message = "La fecha de entrada del huesped es requerida")
            Date fechaEntrada,

            @Schema(
                    description = "Fecha de salida del huesped",
                    example = "30/09/2026"
            )
            @NotBlank(message = "La fecha de salida del huesped es requerida")
            Date fechaSalida
        ) {}