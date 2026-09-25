package com.team.reservas.service;

import com.team.common.client.HabitacionClient;
import com.team.common.client.HuespedClient;
import com.team.common.dto.habitaciones.HabitacionResponse;
import com.team.common.dto.reservas.ReservaRequest;
import com.team.common.dto.reservas.ReservaResponse;
import com.team.common.enums.EstadoHabitacion;
import com.team.common.enums.EstadoRegistro;
import com.team.common.enums.EstadoReserva;
import com.team.common.exceptions.RecursoNoEncontradoException;
import com.team.common.utils.ValoresNumerico;
import com.team.reservas.entity.Reserva;
import com.team.reservas.mapper.ReservaMapper;
import com.team.reservas.repositories.ReservaRepository;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;

    private final ReservaMapper reservaMapper;

    private final HuespedClient huespedClient;

    private final HabitacionClient habitacionClient;

    @Transactional(readOnly = true)
    @Override
    public List<ReservaResponse> listar() {

        log.info("Listando reservas activas");

        return reservaRepository.findAllByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(reservaMapper::entidadAResponse).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ReservaResponse obtenerPorId(Long id) {
        return reservaMapper.entidadAResponse(obtenerReservaActiva(id));
    }

    @Transactional
    @Override
    public ReservaResponse registrar(ReservaRequest request) {

        validarHuespedActivo(request.idHuesped());

        HabitacionResponse habitacion = obtenerHabitacionActiva(request.idHabitacion());

        validarHabitacionDisponible(habitacion);

        Reserva guardada = reservaRepository.save(reservaMapper.requestAEntidad(request));

        log.info("Reserva {} registrada para el huesped {} y la habitacion {}",
                guardada.getIdReserva(), guardada.getIdHuesped(), guardada.getIdHabitacion());

        ocuparHabitacionRemota(guardada.getIdHabitacion());

        return reservaMapper.entidadAResponse(guardada);
    }


    @Override
    public ReservaResponse actualizar(ReservaRequest request, Long id) {

        Reserva reserva = obtenerReservaActiva(id);

        validarMismosParticipantes(reserva, request);

        reserva.actualizarFechas(request.fechaEntrada(), request.fechaSalida());

        log.info("Reserva {} actualizada", id);

        return reservaMapper.entidadAResponse(reservaRepository.saveAndFlush(reserva));
    }


    @Override
    public ReservaResponse cambiarEstado(Long idReserva, Long idEstado) {

        Reserva reserva = obtenerReservaActiva(idReserva);

        EstadoReserva nuevoEstado = EstadoReserva.obtenerEstadoPorCodigo(idEstado);

        reserva.cambiarEstado(nuevoEstado);
        Reserva actualizada = reservaRepository.saveAndFlush(reserva);

        log.info("Reserva {} cambio al estado {}", idReserva, nuevoEstado.name());

        if (EstadoReserva.FINALIZADA.equals(nuevoEstado) || EstadoReserva.CANCELADA.equals(nuevoEstado)) {
            liberarHabitacionRemota(actualizada.getIdHabitacion());
        }

        return reservaMapper.entidadAResponse(actualizada);
    }


    @Override
    public void eliminar(Long id) {

        Reserva reserva = obtenerReservaActiva(id);

        // La entidad rechaza EN_CURSO y los estados históricos antes de tocar Habitaciones.
        reserva.eliminar();

        liberarHabitacionRemota(reserva.getIdHabitacion());
        reservaRepository.save(reserva);

        log.info("Reserva {} eliminada logicamente", id);
    }

    @Transactional(readOnly = true)
    @Override
    public void tieneReservasEnCurso(Long idHuesped) {

        ValoresNumerico.validarNumeroRequerido(idHuesped);

        if (reservaRepository.existsByIdHuespedAndEstadoReservaAndEstadoRegistro(
                idHuesped, EstadoReserva.EN_CURSO, EstadoRegistro.ACTIVO))
            throw new IllegalStateException("El huesped tiene reservas en curso y activas");
    }

    private Reserva obtenerReservaActiva(Long id) {

        log.info("Obteniendo reserva activa con id {}", id);

        ValoresNumerico.validarNumeroRequerido(id);

        return reservaRepository.findByIdReservaAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se ha encontrado la reserva activa con id " + id
                ));
    }

    private void validarHuespedActivo(Long idHuesped) {

        try {
            huespedClient.obtenerPorIdActivo(idHuesped);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException(
                    "No existe un huesped activo con id " + idHuesped
            );
        }
    }

    private HabitacionResponse obtenerHabitacionActiva(Long idHabitacion) {

        try {
            return habitacionClient.obtenerPorId(idHabitacion);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException(
                    "No existe una habitacion activa con id " + idHabitacion
            );
        }
    }

    private void validarHabitacionDisponible(HabitacionResponse habitacion) {

        if (!EstadoHabitacion.DISPONIBLE.equals(habitacion.estadoHabitacion())) {
            throw new IllegalStateException(
                    "La habitacion " + habitacion.idHabitacion()
                            + " no esta disponible, su estado actual es " + habitacion.estadoHabitacion()
            );
        }
    }

    private void validarMismosParticipantes(Reserva reserva, ReservaRequest request) {

        if (!Objects.equals(reserva.getIdHuesped(), request.idHuesped())) {
            throw new IllegalStateException("No se permite cambiar el huesped de una reserva");
        }

        if (!Objects.equals(reserva.getIdHabitacion(), request.idHabitacion())) {
            throw new IllegalStateException("No se permite cambiar la habitacion de una reserva");
        }
    }

    private void ocuparHabitacionRemota(Long idHabitacion) {

        try {
            habitacionClient.ocupar(idHabitacion);
        } catch (FeignException.Conflict e) {
            throw new IllegalStateException(
                    "La habitacion " + idHabitacion + " ya no esta disponible para reservar"
            );
        } catch (FeignException.Forbidden | FeignException.Unauthorized e) {
            log.error("La habitacion rechazo la operacion interna: {}", e.getMessage());
            throw new IllegalStateException(
                    "La habitacion " + idHabitacion + " no permitio la operacion interna; "
                            + "verifique la credencial interna de microservicios"
            );
        }
    }

    private void liberarHabitacionRemota(Long idHabitacion) {

        try {
            habitacionClient.liberar(idHabitacion);

        } catch (FeignException.Conflict e) {
            throw new IllegalStateException(
                    "No se pudo liberar la habitacion " + idHabitacion
            );
        } catch (FeignException.Forbidden | FeignException.Unauthorized e) {
            log.error("La habitacion rechazo la operacion interna: {}", e.getMessage());
            throw new IllegalStateException(
                    "La habitacion " + idHabitacion + " no permitio la operacion interna; "
                            + "verifique la credencial interna de microservicios"
            );
        }
    }

}
