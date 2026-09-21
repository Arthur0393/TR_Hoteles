package com.team.common.dto.huespedes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record HuespedRequest(


        @Schema(
                description = "Nombre del Huesped",
                example = "Sofia"
        )
        @NotNull(message = "El nombre del huesped es requerido")
        @Size(min = 1, max = 50, message = "El nombre del huesped debe tener entre 1 a 50 caracteres")
        String nombre,

        @Schema(
                description = "Apellido Paterno del Huesped",
                example = "Romero"
        )
        @NotNull(message = "El Apellido Paterno del huesped es requerido")
        @Size(min =1, max = 50, message = "El apellido paterno del huesped debe tener de 1 a 5o caracteres")
        String apellidoPaterno,

        @Schema(
                description = "Apellido Materno del Huesped",
                example = "Gomez"
        )
        @NotNull(message = "El Apellido Materno del huesped es requerido")
        @Size(min =1, max = 50, message = "El apellido materno del huesped debe tener de 1 a 50 caracteres")
        String apellidoMaterno,

        @Schema (
                description = "Correo electronico del huesped",
                example = "ejemplo@dominio.com"
        )
        @NotBlank(message = "El correo electronico del huesped es requerido")
        @Size(min = 1, max = 100, message = "El correo electronico debe tener de 1 a 100 caracteres")
        String email,

        @Schema(
                description = "Numero telefonico del huesped",
                example = "1234567890"
        )
        @NotBlank(message = "El telefono del huesped es requerido")
        @Size(min = 10, max = 10, message = "El telefono debe tener exactamente 10 caracteres")
        String telefono,

        @Schema(
                description = "Documento de registro del huesped",
                example = "PASAPORTE"
        )
        @NotBlank(message= "El documento de identificacion del huesped es requerido")
        @Size(min = 1, max = 30, message = "El documento debe tener de 1 a 30 caracteres")
        String documento,

        @Schema(
                description = "Nacionalidad del huesped",
                example = "Mexico"
        )
        @NotBlank(message = "La nacionalidad del huesped es requerida")
        @Size(min = 1, max = 30, message = "La nacionalidad debe tener de 1 a 30 caracteres")
        String Nacionalidad)
{}