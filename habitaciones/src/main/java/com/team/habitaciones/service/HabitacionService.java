package com.team.habitaciones.service;

import com.team.common.dto.habitaciones.HabitacionRequest;
import com.team.common.dto.habitaciones.HabitacionResponse;
import com.team.common.service.CrudService;

public interface HabitacionService extends CrudService <HabitacionRequest, HabitacionResponse> {

    /**
     * Cambia el estado operativo usando su codigo de catalogo.
     */
    HabitacionResponse cambiarEstado(Long id, Long idEstado);
}
