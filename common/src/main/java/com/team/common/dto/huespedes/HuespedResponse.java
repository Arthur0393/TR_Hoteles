package com.team.common.dto.huespedes;

import io.swagger.v3.oas.annotations.media.Schema;

public record HuespedResponse(@Schema(
        description = "Identificador unico del huesped",
        example = "1"
)
      Long idHuesped,

      @Schema(
              description = "Nombre del Huesped",
              example = "Juan Carlos"
      )
      String nombre,

      @Schema(
              description = "Apellido Paterno del Huesped",
              example = "Hernandez"
      )
      String apellidoPaterno,

      @Schema(
              description = "Apellido Materno del Huesped",
              example = "Gomez"
      )
      String apellidoMaterno,

      @Schema (
              description = "Correo electronico del huesped",
              example = "ejemplo@dominio.com"
      )
      String email,

      @Schema(
              description = "Numero telefonico del huesped",
              example = "1234567890"
      )
      String telefono,

      @Schema(
              description = "Documento de registro del huesped",
              example = "PASAPORTE"
      )
      String documento,

      @Schema(
              description = "Nacionalidad del huesped",
              example = "Mexico"
      )
      String nacionalidad,

    @Schema
            (description = "Estado del registro del huesped",
            example = "ACTIVO")
    String estadoRegistro)

{}