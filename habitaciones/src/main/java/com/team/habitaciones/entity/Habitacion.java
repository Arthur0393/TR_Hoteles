package com.team.habitaciones.entity;

import com.team.common.utils.StringCustomUtils;
import com.team.common.utils.ValoresNumerico;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;


@Table
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter

public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "ID_HABITACION")
    private Long id;

    @Column(name = "NUMERO",nullable = false)
    private Number numeroHabitacion;

    @Column(name = "TIPO", length = 50, nullable = false)
    private String tipoHabitacion;

    @Column(name= "PRECIO", nullable = false)
    private BigDecimal precio;

    @Column (name = "CAPACIDAD", nullable = false)
    private Number capacidad;

    @Column(name = "ESTADO_HABITACION", nullable = false)
    private String estadoHabitacion;

    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private String estadoRegistro;


    private void validarDatos(
            Number numeroHabitacion,
            String tipoHabitacion,
            BigDecimal precio,
            Number capacidad,
            String estadoHabitacion,
            String estadoRegistro
    ){
        ValoresNumerico.validarNumeroPositivo(
                numeroHabitacion,
                "El numero de habitacion es requerido y debe ser positivo"
            );

        StringCustomUtils.validarTamanio(
                tipoHabitacion,
                1,
                50,
                "El tipo de habitacion es requerido"
        );

        ValoresNumerico.validarNumeroPositivo(
                precio,
                "El precio es requerido y debe ser positivo"
        );
        ValoresNumerico.validarNumeroPositivo(
                capacidad,
                "La capacidad de la habitacion es requerida y debe ser positiva"
        );
        StringCustomUtils.validarTamanio(
                estadoHabitacion,
                1,
                15,
                "El estado de la habitacion es requerido"
        );

    }


}
