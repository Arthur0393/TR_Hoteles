package com.team.reservas.entity;

import com.team.common.enums.EstadoRegistro;
import com.team.common.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

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

    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_ENTRADA", nullable = false)
    private Date fechaEntrada;

    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_SALIDA", nullable = false)
    private Date fechaSalida;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_RESERVA", nullable = false, length = 15)
    private EstadoReserva estadoReserva = EstadoReserva.CONFIRMADA;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false, length = 15)
    private EstadoRegistro estadoRegistro = EstadoRegistro.ACTIVO;
}
