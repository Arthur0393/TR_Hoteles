package com.team.common.enums;

import com.team.common.exceptions.RecursoNoEncontradoException;
import com.team.common.utils.StringCustomUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public enum Documentacion {


    CREDENCIAL(1L, "Credencial"),
    PASAPORTE(2L, "Pasaporte"),
    CARTILLA_MILITAR(3L, "Cartilla Militar"),
    CURP(4L, "Curp"),;



private final Long codigo;
private final String descripcion;


    public static Documentacion obtenerDocumentacionPorDescripcion(String descripcion)
    {
        StringCustomUtils.validarNoVacio(descripcion,"La descripcion es requerida");

        String descripcionNormalizada =StringCustomUtils.quitarAcentos(descripcion);




        for (Documentacion documentacion:values())
        {
            if (StringCustomUtils.quitarAcentos(documentacion.descripcion).equalsIgnoreCase(descripcionNormalizada))

            {
                return  documentacion;
            }
        }

        throw  new RecursoNoEncontradoException("No existe una categoria para la documentacion : "+descripcion );


    }

    public static Documentacion obtenerDocumentacionPorCodigo(Long codigo)
    {

        for (Documentacion d:values())
        {
            if (Objects.equals(d.codigo,codigo))
                return  d;
        }

        throw  new RecursoNoEncontradoException("Codigo de documentacion no valido: "+ codigo);

    }

}


