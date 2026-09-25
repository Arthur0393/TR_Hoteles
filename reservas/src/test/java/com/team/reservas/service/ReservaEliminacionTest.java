package com.team.reservas.service;

import com.team.common.client.HabitacionClient;
import com.team.common.client.HuespedClient;
import com.team.common.enums.EstadoRegistro;
import com.team.common.enums.EstadoReserva;
import com.team.common.exceptions.RecursoNoEncontradoException;
import com.team.reservas.entity.Reserva;
import com.team.reservas.mapper.ReservaMapper;
import com.team.reservas.repositories.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaEliminacionTest {
    ReservaRepository repository;
    HabitacionClient habitaciones;
    HuespedClient huespedes;
    ReservaServiceImpl service;

    @BeforeEach
    void setup() {
        repository = mock(ReservaRepository.class);
        habitaciones = mock(HabitacionClient.class);
        huespedes = mock(HuespedClient.class);
        service = new ReservaServiceImpl(repository, new ReservaMapper(), huespedes, habitaciones);
    }

    private Reserva reserva(EstadoReserva estado) {
        return Reserva.builder().idReserva(1L).idHabitacion(10L).idHuesped(20L)
                .fechaEntrada(LocalDate.of(2026, 10, 10)).fechaSalida(LocalDate.of(2026, 10, 12))
                .estadoReserva(estado).estadoRegistro(EstadoRegistro.ACTIVO).build();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoReserva.class, names = {"FINALIZADA", "CANCELADA", "EN_CURSO"})
    void entityRejectsDeletionWithoutChangingTheRecord(EstadoReserva estado) {
        Reserva reserva = reserva(estado);
        assertThrows(IllegalStateException.class, reserva::eliminar);
        assertEquals(EstadoRegistro.ACTIVO, reserva.getEstadoRegistro());
        assertEquals(estado, reserva.getEstadoReserva());
    }

    @ParameterizedTest
    @EnumSource(value = EstadoReserva.class, names = {"FINALIZADA", "CANCELADA", "EN_CURSO"})
    void rejectedDeletionNeverTouchesRoomOrPersistsChanges(EstadoReserva estado) {
        Reserva reserva = reserva(estado);
        when(repository.findByIdReservaAndEstadoRegistro(1L, EstadoRegistro.ACTIVO))
                .thenReturn(Optional.of(reserva));

        assertThrows(IllegalStateException.class, () -> service.eliminar(1L));

        assertEquals(EstadoRegistro.ACTIVO, reserva.getEstadoRegistro());
        verify(repository).findByIdReservaAndEstadoRegistro(1L, EstadoRegistro.ACTIVO);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(habitaciones, huespedes);
    }

    @Test
    void confirmedReservationCanBeSoftDeletedAndReleasesItsRoomOnce() {
        Reserva reserva = reserva(EstadoReserva.CONFIRMADA);
        when(repository.findByIdReservaAndEstadoRegistro(1L, EstadoRegistro.ACTIVO))
                .thenReturn(Optional.of(reserva));

        service.eliminar(1L);

        assertEquals(EstadoRegistro.ELIMINADO, reserva.getEstadoRegistro());
        var order = inOrder(habitaciones, repository);
        order.verify(repository).findByIdReservaAndEstadoRegistro(1L, EstadoRegistro.ACTIVO);
        order.verify(habitaciones).liberar(10L);
        order.verify(repository).save(reserva);
        verifyNoMoreInteractions(habitaciones, repository);
        verifyNoInteractions(huespedes);
    }

    @Test
    void failedRoomReleaseIsNotSilentlyIgnored() {
        Reserva reserva = reserva(EstadoReserva.CONFIRMADA);
        when(repository.findByIdReservaAndEstadoRegistro(1L, EstadoRegistro.ACTIVO))
                .thenReturn(Optional.of(reserva));
        doThrow(new IllegalStateException("No se pudo liberar")).when(habitaciones).liberar(10L);

        assertThrows(IllegalStateException.class, () -> service.eliminar(1L));
        verify(repository, never()).save(any());
    }

    @Test
    void missingReservationDoesNotReleaseAnyRoom() {
        when(repository.findByIdReservaAndEstadoRegistro(1L, EstadoRegistro.ACTIVO))
                .thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminar(1L));
        verifyNoInteractions(habitaciones, huespedes);
        verify(repository, never()).save(any());
    }
}
