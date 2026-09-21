package com.team.common.dto.reservas;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

public record ReservaResponse (

        @Schema(
                description = "Identificador unico de la reserva",
                example = "1"

        )
        Long idReserva,

        @Schema(
                description = "Identificador unico del huesped",
                example = "1"
        )
        Long idHuesped,

        @Schema(
                description = "Identificador unico de la habitacion",
                example = "1"
        )
        Long idHabitacion,

        @Schema(
                description = "Fecha de entrada de la reserva",
                example = "19/09/2026"
        )
        Date fechaEntrada,

        @Schema(
                description = "Fecha de salida de la reserva",
                example = "30/09/2026"
        )
        Date fechaSalida
){}