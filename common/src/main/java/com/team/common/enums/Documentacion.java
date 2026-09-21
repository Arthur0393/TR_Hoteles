package com.team.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Documentacion {


    CREDENCIA(1L, "Credencial de identificacion del huesped"),
    PASAPORTE(2L, "Pasaporte del huesped"),
    CARTILLA_MILITAR(3L, "Cartilla Militar del huesped"),
    CURP(4L, "CURP del huesped"),;



private final Long codigo;
private final String descripcion;
}


