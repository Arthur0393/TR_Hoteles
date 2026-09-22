package com.team.huespedes.mapper;

import com.team.common.dto.huespedes.HuespedRequest;
import com.team.common.dto.huespedes.HuespedResponse;
import com.team.common.enums.Documentacion;
import com.team.common.enums.EstadoRegistro;
import com.team.common.mapper.CommonMapper;
import com.team.huespedes.entities.Huesped;
import org.springframework.stereotype.Component;


@Component
public class HuespedMapper implements CommonMapper<HuespedRequest, HuespedResponse, Huesped>
{
    @Override
    public Huesped requestAEntidad(HuespedRequest request) {
       return Huesped.crear(
               request.nombre().trim(),
               request.apellidoPaterno().trim(),
               request.apellidoMaterno().trim(),
               request.email().trim(),
               request.telefono().trim(),
               Documentacion.obtenerDocumentacionPorDescripcion(request.documento()),
               request.numDocumento(),
               request.nacionalidad(),
               EstadoRegistro.ACTIVO

       );
    }

    @Override
    public HuespedResponse entidadAResponse(Huesped entidad) {
        return new  HuespedResponse
                (
                entidad.getIdHuesped(),
                String.join(" ",entidad.getNombre(),
                        entidad.getApellidoPaterno(),
                        entidad.getApellidoMaterno()),
                        entidad.getEmail(),
                        entidad.getTelefono(),
                        entidad.getDocumento().getDescripcion(),
                        entidad.getNumDocumento(),
                        entidad.getNacionalidad()
                );
    }
}
