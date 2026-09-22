package com.team.common.utils;

public class ValoresNumerico {

    public static <N extends Number> void  validarNumeroRequerido (N numero)
    {
        if (numero==null)
        {
            throw  new IllegalArgumentException("El valor numerico es requerido");
        }
    }

    public static void validarNumeroPositivo(Number numero, String mensaje) {
        validarNumeroRequerido(numero);

        if (numero.doubleValue() <= 0) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    public static void validarRangoShort(Short numero, short min, short max, String mensaje) {
        validarNumeroRequerido(numero);

        if (numero < min || numero > max)
            throw new IllegalArgumentException(mensaje);
    }

    public static void validarRangoDouble(Double numero, double min, double max, String mensaje) {
        validarNumeroRequerido(numero);

        if (numero < min || numero > max)
            throw new IllegalArgumentException(mensaje);
    }


}