package com.team.reservas.entity;

import com.team.common.enums.EstadoRegistro;
import com.team.common.enums.EstadoReserva;
import com.team.common.utils.ObjectCustomUtils;
import com.team.common.utils.ValoresNumerico;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "RESERVAS")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RESERVA")
    private Long idReserva;

    @Column(name = "ID_HUESPED", nullable = false, updatable = false)
    private Long idHuesped;

    @Column(name = "ID_HABITACION", nullable = false, updatable = false)
    private Long idHabitacion;

    @Column(name = "FECHA_ENTRADA", nullable = false)
    private LocalDate fechaEntrada;

    @Column(name = "FECHA_SALIDA", nullable = false)
    private LocalDate fechaSalida;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_RESERVA", nullable = false, length = 15)
    private EstadoReserva estadoReserva = EstadoReserva.CONFIRMADA;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false, length = 15)
    private EstadoRegistro estadoRegistro = EstadoRegistro.ACTIVO;

    public static Reserva crear(
            Long idHuesped,
            Long idHabitacion,
            LocalDate fechaEntrada,
            LocalDate fechaSalida
    ) {
        validarDatos(idHuesped, idHabitacion, fechaEntrada, fechaSalida);

        return Reserva.builder()
                .idHuesped(idHuesped)
                .idHabitacion(idHabitacion)
                .fechaEntrada(fechaEntrada)
                .fechaSalida(fechaSalida)
                .estadoReserva(EstadoReserva.CONFIRMADA)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }

    public void actualizarFechas(LocalDate fechaEntrada, LocalDate fechaSalida) {
        validarNoEliminada();
        validarModificable();

        validarFechas(fechaEntrada, fechaSalida);

        if (this.estadoReserva.esSoloFechaSalida()
                && !Objects.equals(this.fechaEntrada, fechaEntrada)) {
            throw new IllegalStateException(
                    "No se puede modificar la fecha de entrada de una reserva con check-in realizado"
            );
        }

        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
    }

    public void cambiarEstado(EstadoReserva nuevoEstado) {
        validarNoEliminada();
        ObjectCustomUtils.validarObjVacios(nuevoEstado, "El estado de la reserva es requerido");

        if (!this.estadoReserva.puedeCambiarA(nuevoEstado)) {
            throw new IllegalStateException(
                    "No se puede cambiar una reserva de " + this.estadoReserva + " a " + nuevoEstado
            );
        }

        this.estadoReserva = nuevoEstado;
    }

    public void eliminar() {
        validarNoEliminada();

        if (!this.estadoReserva.isEliminable()) {
            throw new IllegalStateException(
                    "No se puede eliminar una reserva vigente, primero debe cancelarla o finalizarla"
            );
        }

        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    private void validarNoEliminada() {
        if (EstadoRegistro.ELIMINADO.equals(this.estadoRegistro)) {
            throw new IllegalStateException("La reserva ya esta eliminada");
        }
    }

    private void validarModificable() {
        if (!this.estadoReserva.isActualizable()) {
            throw new IllegalStateException(
                    "No se puede modificar una reserva en estado " + this.estadoReserva
            );
        }
    }

    private static void validarDatos(
            Long idHuesped,
            Long idHabitacion,
            LocalDate fechaEntrada,
            LocalDate fechaSalida
    ) {
        ValoresNumerico.validarNumeroPositivo(
                idHuesped,
                "El identificador del huesped es requerido y debe ser positivo"
        );

        ValoresNumerico.validarNumeroPositivo(
                idHabitacion,
                "El identificador de la habitacion es requerido y debe ser positivo"
        );

        validarFechas(fechaEntrada, fechaSalida);
    }

    private static void validarFechas(LocalDate fechaEntrada, LocalDate fechaSalida) {
        ObjectCustomUtils.validarObjVacios(
                fechaEntrada,
                "La fecha de entrada de la reserva es requerida"
        );

        ObjectCustomUtils.validarObjVacios(
                fechaSalida,
                "La fecha de salida de la reserva es requerida"
        );

        if (!fechaEntrada.isBefore(fechaSalida)) {
            throw new IllegalArgumentException(
                    "La fecha de entrada debe ser anterior a la fecha de salida"
            );
        }
    }
}
