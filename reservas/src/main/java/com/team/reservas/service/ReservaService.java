package com.team.reservas.service;

import com.team.common.dto.reservas.ReservaRequest;
import com.team.common.dto.reservas.ReservaResponse;
import com.team.common.service.CrudService;

public interface ReservaService extends CrudService<ReservaRequest, ReservaResponse> {

    ReservaResponse cambiarEstado(Long idReserva, Long idEstado);

    void tieneReservasEnCurso(Long idHuesped);
}
