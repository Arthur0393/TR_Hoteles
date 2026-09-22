package com.team.huespedes.entities;


import com.team.common.enums.Documentacion;
import com.team.common.enums.EstadoRegistro;
import com.team.common.utils.ObjectCustomUtils;
import com.team.common.utils.StringCustomUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "HUESPEDES")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Getter
public class Huesped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HUESPED")
    Long idHuesped;

    @Column(name = "NOMBRE",nullable = false,length = 50)
    String nombre;

    @Column(name = "APELLIDO_PATERNO",nullable = false,length = 50)
    String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO",nullable = false,length = 50)
    String apellidoMaterno;

    @Column(name = "EMAIL",nullable = false,length = 100)
    String email;

    @Column(name = "TELEFONO",nullable = false,length = 10)
    String telefono;

    @Column(name = "DOCUMENTO",nullable = false,length = 30)
    Documentacion documento;

    @Column(name = "NUM_DOCUMENTO",nullable = false,length = 5)
    String numDocumento;

    @Column(name = "NACIONALIDAD",nullable = false,length = 30)
    String nacionalidad;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "ESTADO_REGISTRO",nullable = false,length = 15)
    EstadoRegistro estadoRegistro=EstadoRegistro.ACTIVO;


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




    private static void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno, String email, String telefono,
                                     String numDocumento, String nacionalidad, Documentacion documento, EstadoRegistro estadoRegistro)
    {

        StringCustomUtils.validarNoVacio(nombre,"El nombre no puede estar vacio");
        StringCustomUtils.validarTamanio(nombre,2,30,"El valor del nombre debe de estar entre 2 y 30 caracteres");

        StringCustomUtils.validarNoVacio(apellidoPaterno,"El apellido paterno  no puede estar vacio");
        StringCustomUtils.validarTamanio(apellidoPaterno,2,30,"El valor del apellido paterno debe de estar entre 2 y 30 caracteres");

        StringCustomUtils.validarNoVacio(apellidoMaterno,"El apellido materno no puede estar vacio");
        StringCustomUtils.validarTamanio(apellidoMaterno,2,30,"El valor del apellido materno debe de estar entre 2 y 30 caracteres");

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
