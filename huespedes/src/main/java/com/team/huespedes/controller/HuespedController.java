package com.team.huespedes.controller;

import com.team.common.controller.CrudController;
import com.team.common.dto.CustomErrorResponse;
import com.team.common.dto.huespedes.HuespedRequest;
import com.team.common.dto.huespedes.HuespedResponse;
import com.team.huespedes.service.HuespedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST para administrar huéspedes del hotel.
 *
 * <p>Los métodos CRUD se sobrescriben para documentar su contrato en OpenAPI.
 * La implementación y las validaciones se heredan del controlador común.</p>
 */
@RestController
@Validated
@Tag(name = "Huespedes", description = "Consulta y administración de huéspedes y su eliminación lógica.")
public class HuespedController extends CrudController<HuespedRequest, HuespedResponse, HuespedService> {

    public HuespedController(HuespedService service) {
        super(service);
    }

    @Override
    @Operation(
            summary = "Listar huéspedes con registro activo",
            description = "Devuelve los registros ACTIVO. Los eliminados lógicamente no se incluyen."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado obtenido; puede estar vacío.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = HuespedResponse.class)))
            )
    })
    @GetMapping
    public ResponseEntity<List<HuespedResponse>> listar() {
        return super.listar();
    }

    @Override
    @Operation(
            summary = "Consultar huésped por ID",
            description = "Busca un registro ACTIVO. Un registro eliminado se considera no encontrado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro encontrado.",
                    content = @Content(schema = @Schema(implementation = HuespedResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El ID no es positivo.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe un registro ACTIVO de huésped con ese ID.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<HuespedResponse> obtenerPorId(
            @Parameter(description = "ID de huésped", example = "1", required = true)
            @PathVariable Long id
    ) {
        return super.obtenerPorId(id);
    }

    @Override
    @Operation(
            summary = "Registrar huésped",
            description = "Crea un huésped con registro ACTIVO. Email, teléfono y la combinación de tipo "
                    + "y número de documento deben ser únicos entre huéspedes activos."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Registro creado.",
                    content = @Content(schema = @Schema(implementation = HuespedResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Los datos no cumplen las validaciones.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Otro huésped activo ya usa el email, teléfono o la combinación de tipo y "
                            + "número de documento.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<HuespedResponse> registrar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nombre, apellidos, email, teléfono, tipo de documento, sus últimos cinco "
                            + "dígitos y nacionalidad.",
                    required = true
            )
            @RequestBody HuespedRequest request
    ) {
        return super.registrar(request);
    }

    @Override
    @Operation(
            summary = "Actualizar huésped",
            description = "Actualiza los datos personales y de contacto del huésped activo. Conserva el "
                    + "estado del registro y valida la unicidad entre huéspedes activos."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro actualizado.",
                    content = @Content(schema = @Schema(implementation = HuespedResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El ID o los datos no son válidos.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe un registro ACTIVO de huésped con ese ID.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Otro huésped activo ya usa el email, teléfono o la combinación de tipo y "
                            + "número de documento.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<HuespedResponse> actualizar(
            @Parameter(description = "ID de huésped", example = "1", required = true)
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nombre, apellidos, email, teléfono, tipo de documento, sus últimos cinco "
                            + "dígitos y nacionalidad.",
                    required = true
            )
            @RequestBody HuespedRequest request
    ) {
        return super.actualizar(id, request);
    }

    @Override
    @Operation(
            summary = "Eliminar huésped",
            description = "Realiza una eliminación lógica. No se permite eliminar un huésped con reservas "
                    + "EN_CURSO."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Registro eliminado; respuesta sin cuerpo.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El ID no es positivo.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe un registro ACTIVO de huésped con ese ID.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El huésped tiene reservas EN_CURSO.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de huésped", example = "1", required = true)
            @PathVariable Long id
    ) {
        return super.eliminar(id);
    }

    @Operation(
            summary = "Consultar huésped sin filtrar su estado",
            description = "Devuelve un huésped por ID, tanto ACTIVO como ELIMINADO. A diferencia de GET "
                    + "/{id}, no filtra por estado del registro."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Huésped encontrado.",
                    content = @Content(schema = @Schema(implementation = HuespedResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El ID no es positivo.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe un huésped con ese ID.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    @GetMapping("/id-huesped/{id}")
    public HuespedResponse obtenerPorIdHuesped(
            @Parameter(description = "ID del huésped", example = "1", required = true)
            @PathVariable @Positive(message = "El ID debe ser positivo") Long id
    ) {
        return service.obtenerPorIdSinEstado(id);
    }
}
