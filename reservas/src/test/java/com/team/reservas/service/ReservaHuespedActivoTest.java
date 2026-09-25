package com.team.reservas.service;

import com.team.common.client.HabitacionClient;
import com.team.common.client.HuespedClient;
import com.team.common.dto.habitaciones.HabitacionResponse;
import com.team.common.dto.huespedes.HuespedResponse;
import com.team.common.dto.reservas.ReservaRequest;
import com.team.common.enums.EstadoHabitacion;
import com.team.common.enums.EstadoRegistro;
import com.team.common.enums.EstadoReserva;
import com.team.common.enums.TipoHabitacion;
import com.team.common.exceptions.RecursoNoEncontradoException;
import com.team.reservas.entity.Reserva;
import com.team.reservas.mapper.ReservaMapper;
import com.team.reservas.repositories.ReservaRepository;
import feign.Feign;
import feign.FeignException;
import feign.Response;
import feign.Retryer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.support.SpringMvcContract;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservaHuespedActivoTest {
    ReservaRepository repository;
    HabitacionClient habitaciones;
    ReservaServiceImpl service;

    @BeforeEach
    void setup() {
        repository = mock(ReservaRepository.class);
        habitaciones = mock(HabitacionClient.class);
        // Cliente Feign real: ejercita la ruta declarada en HuespedClient, no un mock de ese método.
        HuespedClient huespedes = Feign.builder().contract(new SpringMvcContract())
                .retryer(Retryer.NEVER_RETRY)
                .client((request, options) -> {
                    String path = URI.create(request.url()).getPath();
                    // El contrato histórico devuelve también al huésped eliminado 82.
                    int status = switch (path) {
                        case "/81", "/id-huesped/81", "/id-huesped/82" -> 200;
                        case "/83", "/id-huesped/83" -> 503;
                        default -> 404;
                    };
                    return Response.builder().request(request).status(status).reason("test")
                            .headers(Map.of()).body("{}", StandardCharsets.UTF_8).build();
                })
                .decoder((response, type) -> new HuespedResponse(81L, "Ana Prueba", "qa@example.com",
                        "5512345678", "Pasaporte", "81234", "Mexico"))
                .target(HuespedClient.class, "http://huespedes.test");
        service = new ReservaServiceImpl(repository, new ReservaMapper(), huespedes, habitaciones);
    }

    private ReservaRequest request(long guestId) {
        return new ReservaRequest(guestId, 10L, LocalDate.of(2026, 10, 10), LocalDate.of(2026, 10, 12));
    }

    @Test
    void deletedGuestAvailableInHistoryCannotCreateReservationOrTouchRoom() {
        var error = assertThrows(RecursoNoEncontradoException.class, () -> service.registrar(request(82L)));
        assertTrue(error.getMessage().contains("huesped activo"));
        verifyNoInteractions(repository, habitaciones);
    }

    @Test
    void missingGuestCannotCreateReservationOrTouchRoom() {
        assertThrows(RecursoNoEncontradoException.class, () -> service.registrar(request(999L)));
        verifyNoInteractions(repository, habitaciones);
    }

    @Test
    void unavailableGuestServiceDoesNotAllowReservation() {
        var error = assertThrows(FeignException.class, () -> service.registrar(request(83L)));
        assertEquals(503, error.status());
        verifyNoInteractions(repository, habitaciones);
    }

    @Test
    void activeGuestCanReserveAvailableRoom() {
        when(habitaciones.obtenerPorId(10L)).thenReturn(new HabitacionResponse(10L, 101,
                TipoHabitacion.DOBLE, BigDecimal.valueOf(1200), 2,
                EstadoHabitacion.DISPONIBLE, EstadoRegistro.ACTIVO));
        when(repository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.registrar(request(81L));

        assertEquals(81L, response.idHuesped());
        assertEquals(EstadoReserva.CONFIRMADA, response.estadoReserva());
        var order = inOrder(repository, habitaciones);
        order.verify(habitaciones).obtenerPorId(10L);
        order.verify(repository).save(any(Reserva.class));
        order.verify(habitaciones).ocupar(10L);
    }
}
