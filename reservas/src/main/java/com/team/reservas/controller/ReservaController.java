package com.team.reservas.controller;

import com.team.common.controller.CrudController;
import com.team.common.dto.CustomErrorResponse;
import com.team.common.dto.reservas.ReservaRequest;
import com.team.common.dto.reservas.ReservaResponse;
import com.team.reservas.service.ReservaService;
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
 * API REST para administrar reservas del hotel.
 *
 * <p>Los métodos CRUD se sobrescriben para documentar su contrato en OpenAPI.
 * La implementación y las validaciones se heredan del controlador común.</p>
 */
@RestController
@Validated
@Tag(name = "Reservas", description = "Consulta y administración de reservas y su eliminación lógica.")
public class ReservaController extends CrudController<ReservaRequest, ReservaResponse, ReservaService> {

    public ReservaController(ReservaService service) {
        super(service);
    }

    @Override
    @Operation(
            summary = "Listar reservas con registro activo",
            description = "Devuelve los registros ACTIVO. Los eliminados lógicamente no se incluyen."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado obtenido; puede estar vacío.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservaResponse.class)))
            )
    })
    @GetMapping
    public ResponseEntity<List<ReservaResponse>> listar() {
        return super.listar();
    }

    @Override
    @Operation(
            summary = "Consultar reserva por ID",
            description = "Busca un registro ACTIVO. Un registro eliminado se considera no encontrado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro encontrado.",
                    content = @Content(schema = @Schema(implementation = ReservaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El ID no es positivo.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe un registro ACTIVO de reserva con ese ID.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> obtenerPorId(
            @Parameter(description = "ID de reserva", example = "1", required = true)
            @PathVariable Long id
    ) {
        return super.obtenerPorId(id);
    }

    @Override
    @Operation(
            summary = "Registrar reserva",
            description = "Crea una reserva CONFIRMADA con registro ACTIVO. Requiere huésped y habitación "
                    + "activos, habitación DISPONIBLE y sin reserva vigente. La fecha de entrada debe "
                    + "ser anterior a la salida. La habitación pasa a OCUPADA."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Registro creado.",
                    content = @Content(schema = @Schema(implementation = ReservaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Los datos no cumplen las validaciones.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe un huésped o una habitación activos con los IDs indicados.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "La habitación no está disponible o ya tiene una reserva vigente.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ReservaResponse> registrar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "IDs del huésped y la habitación, fecha de entrada y fecha de salida.",
                    required = true
            )
            @RequestBody ReservaRequest request
    ) {
        return super.registrar(request);
    }

    @Override
    @Operation(
            summary = "Actualizar reserva",
            description = "En CONFIRMADA permite modificar ambas fechas; en EN_CURSO solo la salida, "
                    + "enviando la misma entrada. La entrada debe ser anterior a la salida. Deben "
                    + "conservarse los IDs del huésped y la habitación. FINALIZADA y CANCELADA no "
                    + "admiten modificaciones."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro actualizado.",
                    content = @Content(schema = @Schema(implementation = ReservaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El ID o los datos no son válidos.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe un registro ACTIVO de reserva con ese ID.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El estado no permite la modificación, se intenta cambiar la entrada tras "
                            + "check-in o se cambian los participantes.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponse> actualizar(
            @Parameter(description = "ID de reserva", example = "1", required = true)
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "IDs del huésped y la habitación, fecha de entrada y fecha de salida.",
                    required = true
            )
            @RequestBody ReservaRequest request
    ) {
        return super.actualizar(id, request);
    }

    @Override
    @Operation(
            summary = "Eliminar reserva",
            description = "Elimina lógicamente una reserva CONFIRMADA y libera su habitación. "
                    + "EN_CURSO no se puede eliminar; FINALIZADA y CANCELADA son de solo consulta histórica."
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
                    description = "No existe un registro ACTIVO de reserva con ese ID.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "La reserva está EN_CURSO, FINALIZADA o CANCELADA, o no se pudo liberar su habitación.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de reserva", example = "1", required = true)
            @PathVariable Long id
    ) {
        return super.eliminar(id);
    }

    @Operation(
            summary = "Cambiar el estado de la reserva",
            description = "Códigos: 1 = CONFIRMADA, 2 = EN_CURSO, 3 = FINALIZADA y 4 = CANCELADA. "
                    + "Transiciones permitidas: CONFIRMADA a EN_CURSO (check-in) o CANCELADA; "
                    + "EN_CURSO a FINALIZADA (check-out). Check-out y cancelación liberan la "
                    + "habitación. No se permite volver a CONFIRMADA ni repetir el estado actual."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado actualizado.",
                    content = @Content(schema = @Schema(implementation = ReservaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Alguno de los identificadores no es positivo.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "La reserva activa o el código de estado no existen.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "La transición no está permitida o no se pudo liberar la habitación.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    @PatchMapping("/{idReserva}/estado/{idEstado}")
    public ResponseEntity<ReservaResponse> cambiarEstado(
            @Parameter(description = "ID de la reserva", example = "1", required = true)
            @PathVariable @Positive(message = "El ID debe ser positivo") Long idReserva,

            @Parameter(description = "Código: 1 CONFIRMADA, 2 EN_CURSO, 3 FINALIZADA, 4 CANCELADA", example = "2", required = true)
            @PathVariable @Positive(message = "El ID debe ser positivo") Long idEstado
    ) {
        return ResponseEntity.ok(service.cambiarEstado(idReserva, idEstado));
    }

    @Operation(
            summary = "Consultar si el huésped tiene reservas en curso (uso interno)",
            description = "Usado por el microservicio de huéspedes antes de una eliminación. Lanza "
                    + "un error 409 si existe una reserva EN_CURSO con registro ACTIVO para ese ID. No "
                    + "comprueba la existencia del huésped; si no hay coincidencias responde sin cuerpo."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "El huésped no tiene reservas en curso; respuesta sin cuerpo.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El huésped tiene reservas en curso.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El ID no es positivo.",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class))
            )
    })
    @GetMapping("/id-huesped/{idHuesped}/en-curso")
    public void tieneReservasEnCurso(
            @PathVariable @Positive(message = "El ID del huesped debe ser positivo") Long idHuesped
    ) {
        service.tieneReservasEnCurso(idHuesped);
    }
}
