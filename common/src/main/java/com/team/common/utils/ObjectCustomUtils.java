package com.team.common.utils;

public class ObjectCustomUtils {

    public static void validarObjVacios(Object object,String mensaje)
    {

        if (object==null)
            throw  new IllegalArgumentException(mensaje);


    }

}
