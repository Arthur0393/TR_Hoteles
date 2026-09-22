package com.team.common.dto.huespedes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record HuespedRequest(


        @Schema(
                description = "Nombre del Huesped",
                example = "Sofia"
        )
        @NotBlank(message = "El nombre del huesped es requerido")
        @Size(min = 2, max = 50, message = "El nombre del huesped debe tener entre 2 a 50 caracteres")
        String nombre,

        @Schema(
                description = "Apellido Paterno del Huesped",
                example = "Romero"
        )
        @NotBlank(message = "El Apellido Paterno del huesped es requerido")
        @Size(min =2, max = 50, message = "El apellido paterno del huesped debe tener de 2 a 50 caracteres")
        String apellidoPaterno,

        @Schema(
                description = "Apellido Materno del Huesped",
                example = "Gomez"
        )
        @NotBlank(message = "El Apellido Materno del huesped es requerido")
        @Size(min =2, max = 50, message = "El apellido materno del huesped debe tener de 2 a 50 caracteres")
        String apellidoMaterno,

        @Schema (
                description = "Correo electronico del huesped",
                example = "ejemplo@dominio.com"
        )
        @Email(message = "El email debe de ser un formato valido")
        @NotBlank(message = "El correo electronico del huesped es requerido")
        @Size(min = 1, max = 100, message = "El correo electronico debe tener de 1 a 100 caracteres")
        String email,

        @Schema(
                description = "Numero telefonico del huesped",
                example = "1234567890"
        )

        @NotBlank(message = "El telefono del huesped es requerido")
        @Pattern(regexp = "^\\d{10}$", message = "El telefono debe tener exactamente 10 digitos")
        String telefono,

        @Schema(
                description = "Documento de registro del huesped",
                example = "PASAPORTE"
        )
        @NotBlank(message= "El documento de identificacion del huesped es requerido")
        @Pattern(regexp = "CREDENCIAL|PASAPORTE|CARTILLA_MILITAR|CURP",
                message = "Tipo de documento no valido, use: CREDENCIAL, PASAPORTE, CARTILLA_MILITAR o CURP")
        String documento,

        @Schema(
                description = "5 ultimos Digitos que complementan el documento",
                example = "89201"
        )
        @NotBlank(message= "Los 5 ultimos digitos del documento son requeridos")
        @Pattern(regexp = "^\\d{5}$", message = "Los 5 ultimos digitos del documento deben ser exactamente 5 numeros")
        String numDocumento,


        @Schema(
                description = "Nacionalidad del huesped",
                example = "Mexico"
        )
        @NotBlank(message = "La nacionalidad del huesped es requerida")
        @Size(min = 1, max = 30, message = "La nacionalidad debe tener de 1 a 30 caracteres")
        String nacionalidad
) {}