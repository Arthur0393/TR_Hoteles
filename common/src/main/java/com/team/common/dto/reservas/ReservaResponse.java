package com.team.common.dto.reservas;

import com.team.common.enums.EstadoRegistro;
import com.team.common.enums.EstadoReserva;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

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
                example = "2026-09-19"
        )
        LocalDate fechaEntrada,

        @Schema(
                description = "Fecha de salida de la reserva",
                example = "2026-09-30"
        )
        LocalDate fechaSalida,

        @Schema(
                description = "Estado de la reserva",
                example = "CONFIRMADA"
        )
        EstadoReserva estadoReserva,

        @Schema(
                description = "Estado del registro de la reserva",
                example = "ACTIVO"
        )
        EstadoRegistro estadoRegistro
){}
