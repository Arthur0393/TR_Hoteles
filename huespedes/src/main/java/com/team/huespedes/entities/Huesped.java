package com.team.huespedes.entities;


import com.team.common.enums.Documentacion;
import com.team.common.enums.EstadoRegistro;
import com.team.common.utils.ObjectCustomUtils;
import com.team.common.utils.StringCustomUtils;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "HUESPEDES")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Getter
public class Huesped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HUESPED")
    private Long idHuesped;

    @Column(name = "NOMBRE",nullable = false,length = 50)
    private  String nombre;

    @Column(name = "APELLIDO_PATERNO",nullable = false,length = 50)
    private  String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO",nullable = false,length = 50)
    private  String apellidoMaterno;

    @Column(name = "EMAIL",nullable = false,length = 100)
    private  String email;

    @Column(name = "TELEFONO",nullable = false,length = 10)
    private  String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "DOCUMENTO",nullable = false,length = 30)
    private  Documentacion documento;

    @Column(name = "NUM_DOCUMENTO",nullable = false,length = 5)
    private  String numDocumento;

    @Column(name = "NACIONALIDAD",nullable = false,length = 30)
    private  String nacionalidad;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "ESTADO_REGISTRO",nullable = false,length = 15)
    private EstadoRegistro estadoRegistro=EstadoRegistro.ACTIVO;


    public static Huesped crear(String nombre, String apellidoPaterno, String apellidoMaterno, String email, String telefono, Documentacion documento,
                       String numDocumento, String nacionalidad, EstadoRegistro estadoRegistro) {

        validarDatos(nombre,apellidoPaterno,apellidoMaterno,email,telefono,numDocumento,nacionalidad,documento,estadoRegistro);


        return Huesped.builder()
                .nombre(nombre)
                .apellidoPaterno(apellidoPaterno)
                .apellidoMaterno(apellidoMaterno)
                .email(email)
                .telefono(telefono)
                .documento(documento)
                .numDocumento(numDocumento)
                .nacionalidad(nacionalidad)
                .estadoRegistro(estadoRegistro)
                .build();


    }

    public void  actualizar(String nombre, String apellidoPaterno, String apellidoMaterno, String email, String telefono, Documentacion documento,
                       String numDocumento, String nacionalidad, EstadoRegistro estadoRegistro) {

        validarDatos(nombre,apellidoPaterno,apellidoMaterno,email,telefono,numDocumento,nacionalidad,documento,estadoRegistro);


        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.email = email;
        this.telefono = telefono;
        this.documento = documento;
        this.numDocumento = numDocumento;
        this.nacionalidad = nacionalidad;
        this.estadoRegistro = estadoRegistro;
    }

    public void eliminar()
    {

        if (EstadoRegistro.ELIMINADO.equals(this.estadoRegistro)) {
            throw new IllegalStateException("El huesped ya se encuentra eliminado");
        }

        this.estadoRegistro=EstadoRegistro.ELIMINADO;

    }





    private static void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno, String email, String telefono,
                                     String numDocumento, String nacionalidad, Documentacion documento, EstadoRegistro estadoRegistro)
    {

        StringCustomUtils.validarNoVacio(nombre,"El nombre no puede estar vacio");
        StringCustomUtils.validarTamanio(nombre,2,50,"El valor del nombre debe de estar entre 2 y 50 caracteres");

        StringCustomUtils.validarNoVacio(apellidoPaterno,"El apellido paterno  no puede estar vacio");
        StringCustomUtils.validarTamanio(apellidoPaterno,2,50,"El valor del apellido paterno debe de estar entre 2 y 50 caracteres");

        StringCustomUtils.validarNoVacio(apellidoMaterno,"El apellido materno no puede estar vacio");
        StringCustomUtils.validarTamanio(apellidoMaterno,2,50,"El valor del apellido materno debe de estar entre 2 y 50 caracteres");

        StringCustomUtils.validarNoVacio(email,"El email no puede estar vacio");
        StringCustomUtils.validarTamanio(email,1,100,"El email  debe de estar entre 1 y 100 caracteres");

        StringCustomUtils.validarNoVacio(telefono,"El telefono no puede estar vacio");
        StringCustomUtils.validarTamanio(telefono,10,10,"EL telefono debe de contener exactamente 10 caracteres");

        ObjectCustomUtils.validarObjVacios(documento,"El documento no puede estar vacio");


        StringCustomUtils.validarNoVacio(numDocumento,"El numero de documento no puede estar vacio");
        StringCustomUtils.validarTamanio(numDocumento,5,5,"Se deben de colocar los 5 ultimos digitos forzosamente de su documento de identificacion");


        StringCustomUtils.validarNoVacio(nacionalidad,"La nacionalidad no puede estar vacia");
        StringCustomUtils.validarTamanio(nacionalidad,1,30,"La nacionalidad debe de tener entre 1 y 30 caracteres");

        ObjectCustomUtils.validarObjVacios(estadoRegistro,"El estado del registro no puede estar vacio ");

    }


}
