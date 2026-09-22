package com.team.huespedes.service;

import com.team.common.dto.huespedes.HuespedRequest;
import com.team.common.dto.huespedes.HuespedResponse;
import com.team.common.service.CrudService;

public interface HuespedService extends CrudService <HuespedRequest, HuespedResponse> {

    HuespedResponse obtenerPorIdSinEstado(Long id);

}
