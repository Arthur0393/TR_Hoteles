package com.team.common.dto.huespedes;

import io.swagger.v3.oas.annotations.media.Schema;

public record HuespedResponse(@Schema(
        description = "Identificador unico del huesped",
        example = "1"
)
      Long idHuesped,

      @Schema(
              description = "Nombre completo del huesped",
              example = "Juan Carlos Hernandez Martinez"
      )
      String nombre,

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
                                      description = "5 digitos finales que acompañan al documento",
                                      example = "83727") String numDocumento,



      @Schema(
              description = "Nacionalidad del huesped",
              example = "Mexico"
      )
      String nacionalidad)



{}