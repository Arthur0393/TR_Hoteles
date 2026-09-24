package com.team.habitaciones.entity;

import com.team.common.enums.EstadoHabitacion;
import com.team.common.enums.EstadoRegistro;
import com.team.common.utils.StringCustomUtils;
import com.team.common.utils.ValoresNumerico;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;



@Entity
@Table(name = "HABITACIONES")
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
    private Integer numeroHabitacion;

    @Column(name = "TIPO", length = 50, nullable = false)
    private String tipoHabitacion;

    @Column(name= "PRECIO", nullable = false)
    private BigDecimal precio;

    @Column (name = "CAPACIDAD", nullable = false)
    private Integer capacidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_HABITACION",
            nullable = false,
            length = 15)
    private EstadoHabitacion estadoHabitacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;



    private void validarDatos(
            Integer numeroHabitacion,
            String tipoHabitacion,
            BigDecimal precio,
            Integer capacidad,
            EstadoHabitacion estadoHabitacion,
            EstadoRegistro estadoRegistro
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

        if (estadoHabitacion == null) {
            throw new IllegalArgumentException("El estado de la habitacion es requerido");
        }

        if (estadoRegistro == null) {
            throw new IllegalArgumentException("El estado del registro es requerido");
        }
    }


    private void validarNoEliminado(){

        if(EstadoRegistro.ELIMINADO.equals(this.estadoRegistro)){
            throw new IllegalArgumentException(
                    "La habitacion ya esta eliminada"
            );
        }
    }

    public void eliminar() {
        validarNoEliminado();

        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }


    public void cambiarEstado(EstadoHabitacion nuevoEstado) {
        validarNoEliminado();

        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado de la habitacion es requerido");
        }

        boolean estaOcupada = EstadoHabitacion.OCUPADA.equals(this.estadoHabitacion);
        boolean seQuiereLiberar = EstadoHabitacion.DISPONIBLE.equals(nuevoEstado);

        if (estaOcupada && seQuiereLiberar) {
            throw new IllegalStateException(
                    "No se puede cambiar manualmente una habitacion ocupada a disponible"
            );
        }

        this.estadoHabitacion = nuevoEstado;
    }


    public void ocupar() {
        validarNoEliminado();

        if (!EstadoHabitacion.DISPONIBLE.equals(this.estadoHabitacion)) {
            throw new IllegalStateException(
                    "La habitacion " + this.id + " no esta disponible, su estado actual es "
                            + this.estadoHabitacion
            );
        }

        this.estadoHabitacion = EstadoHabitacion.OCUPADA;
    }


    public void liberar() {
        validarNoEliminado();

        if (!EstadoHabitacion.OCUPADA.equals(this.estadoHabitacion)) {
            throw new IllegalStateException(
                    "No se puede liberar la habitacion " + this.id
                            + " porque su estado actual es " + this.estadoHabitacion
            );
        }

        this.estadoHabitacion = EstadoHabitacion.DISPONIBLE;
    }


    public void  actualizar(
            Integer numeroHabitacion,
            String tipoHabitacion,
            BigDecimal precio,
            Integer capacidad,
            EstadoHabitacion estadoHabitacion,
            EstadoRegistro estadoRegistro
    ) {
        validarNoEliminado();

        validarDatos(
                numeroHabitacion,
                tipoHabitacion,
                precio,
                capacidad,
                estadoHabitacion,
                estadoRegistro
        );
        this.numeroHabitacion = numeroHabitacion;
        this.tipoHabitacion = tipoHabitacion;
        this.precio = precio;
        this.capacidad = capacidad;
        this.estadoHabitacion = estadoHabitacion;
        this.estadoRegistro = estadoRegistro;
    }
}
