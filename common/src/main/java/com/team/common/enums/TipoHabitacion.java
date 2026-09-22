package com.team.common.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoHabitacion {

    SENCILLA(1L, "Habitacion Sencilla"),
    DOBLE(2L, "Habitacion Doble"),
    SUITE(3L, "Habitacion Suite"),
    ESTANDAR(4L, "Habitacion Estandar");
    private final Long codigo;

    private final String descripcion;
}
