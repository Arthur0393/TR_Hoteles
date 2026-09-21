package com.team.common.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EstadoReserva {

    CONFIRMADA(1L, "Reserva creada"),
    EN_CURSO(2L, "Chek-in realizado"),
    FINALIZADA(3L, "Chek-out realizado"),
    CANCELADA(4L, "Reserva cancelada");

    private final Long codigo;

    private final String descripcion;
}
