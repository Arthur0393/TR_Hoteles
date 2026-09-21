package com.team.common.dto.reservas;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

public record ReservaResponse (

        @Schema(
                description = "Identificador de la reserva"
        )
        Long idReserva,

        Long idHuesped,

        Long idHabitacion,

        Date fechaEntrada,

        Date fechaSalida
){
}
