package com.team.reservas.mapper;

import com.team.common.dto.reservas.ReservaRequest;
import com.team.common.dto.reservas.ReservaResponse;
import com.team.common.mapper.CommonMapper;
import com.team.reservas.entity.Reserva;
import org.springframework.stereotype.Component;

@Component
public class ReservaMapper implements CommonMapper<ReservaRequest, ReservaResponse, Reserva> {

    @Override
    public Reserva requestAEntidad(ReservaRequest request) {
        if (request == null) {
            return null;
        }

        return Reserva.crear(
                request.idHuesped(),
                request.idHabitacion(),
                request.fechaEntrada(),
                request.fechaSalida()
        );
    }

    @Override
    public ReservaResponse entidadAResponse(Reserva reserva) {
        if (reserva == null) {
            return null;
        }

        return new ReservaResponse(
                reserva.getIdReserva(),
                reserva.getIdHuesped(),
                reserva.getIdHabitacion(),
                reserva.getFechaEntrada(),
                reserva.getFechaSalida(),
                reserva.getEstadoReserva(),
                reserva.getEstadoRegistro()
        );
    }
}
