package com.team.habitaciones.mapper;

import com.team.common.dto.habitaciones.HabitacionRequest;
import com.team.common.dto.habitaciones.HabitacionResponse;
import com.team.common.enums.EstadoHabitacion;
import com.team.common.enums.EstadoRegistro;
import com.team.common.enums.TipoHabitacion;
import com.team.common.mapper.CommonMapper;
import com.team.habitaciones.entity.Habitacion;
import org.springframework.stereotype.Component;

@Component
public class HabitacionesMapper
        implements CommonMapper <HabitacionRequest, HabitacionResponse, Habitacion>{

    @Override
    public Habitacion requestAEntidad(HabitacionRequest request) {
        if (request == null) {
            return null;
        }

        return Habitacion.builder()
                .numeroHabitacion(request.numeroHabitacion())
                .tipoHabitacion(request.tipoHabitacion().name())
                .precio(request.precio())
                .capacidad(request.capacidad())
                .estadoHabitacion(EstadoHabitacion.DISPONIBLE.name())
                .estadoRegistro(EstadoRegistro.ACTIVO.name())
                .build();
    }

    @Override
    public HabitacionResponse entidadAResponse(Habitacion habitacion){
        if(habitacion == null) {
            return null;
        }
        return new HabitacionResponse(
                habitacion.getId(),
                habitacion.getNumeroHabitacion(),
                TipoHabitacion.valueOf(habitacion.getTipoHabitacion()),
                habitacion.getPrecio(),
                habitacion.getCapacidad(),
                EstadoHabitacion.valueOf(habitacion.getEstadoHabitacion()),
                EstadoRegistro.valueOf(habitacion.getEstadoRegistro())
        );
    }
}
