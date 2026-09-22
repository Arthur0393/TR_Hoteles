package com.team.habitaciones.controller;

import com.team.common.controller.CrudController;
import com.team.common.dto.CustomErrorResponse;
import com.team.common.dto.habitaciones.HabitacionRequest;
import com.team.common.dto.habitaciones.HabitacionResponse;
import com.team.habitaciones.service.HabitacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST para administrar las habitaciones del hotel.
 *
 * <p>La eliminación es lógica: el registro permanece almacenado, pero deja de aparecer en las
 * consultas. Los métodos CRUD se sobrescriben únicamente para documentar en OpenAPI el contrato
 * específico de habitaciones.</p>
 */
@RestController
@RequestMapping
@Validated
@Tag(
        name = "Habitaciones",
        description = "Consulta y administración de habitaciones activas, "
                + "sus datos generales y su estado operativo."
)
public class HabitacionController extends CrudController<
        HabitacionRequest,
        HabitacionResponse,
        HabitacionService> {

    public HabitacionController(HabitacionService service) {
        super(service);
    }

    @Override
    @Operation(
            summary = "Listar habitaciones activas",
            description = "Devuelve todas las habitaciones con registro ACTIVO. "
                    + "Las eliminadas lógicamente no se incluyen."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Listado obtenido; puede estar vacío.",
            content = @Content(
                    array = @ArraySchema(
                            schema = @Schema(implementation = HabitacionResponse.class)
                    )
            )
    )
    @GetMapping
    public ResponseEntity<List<HabitacionResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Override
    @Operation(
            summary = "Consultar una habitación por ID",
            description = "Busca una habitación activa. "
                    + "Una habitación eliminada se considera no encontrada."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Habitación encontrada.",
                    content = @Content(
                            schema = @Schema(implementation = HabitacionResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El ID no es positivo.",
                    content = @Content(
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe una habitación activa con ese ID.",
                    content = @Content(
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<HabitacionResponse> obtenerPorId(
            @Parameter(
                    description = "ID de la habitación",
                    example = "1",
                    required = true
            )
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Override
    @Operation(
            summary = "Registrar una habitación",
            description = "Crea una habitación con estado DISPONIBLE y registro ACTIVO. "
                    + "El número debe ser único entre las habitaciones activas."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Habitación registrada.",
                    content = @Content(
                            schema = @Schema(implementation = HabitacionResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Los datos no cumplen las validaciones.",
                    content = @Content(
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe una habitación activa con ese número.",
                    content = @Content(
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<HabitacionResponse> registrar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Número, tipo, precio y capacidad de la nueva habitación.",
                    required = true
            )
            @RequestBody HabitacionRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.registrar(request));
    }

    @Override
    @Operation(
            summary = "Actualizar una habitación",
            description = "Sustituye número, tipo, precio y capacidad. "
                    + "Conserva el estado operativo y el estado del registro."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Habitación actualizada.",
                    content = @Content(
                            schema = @Schema(implementation = HabitacionResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El ID o los datos no son válidos.",
                    content = @Content(
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe una habitación activa con ese ID.",
                    content = @Content(
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Otra habitación activa ya usa ese número.",
                    content = @Content(
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<HabitacionResponse> actualizar(
            @Parameter(
                    description = "ID de la habitación",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos generales de la habitación.",
                    required = true
            )
            @RequestBody HabitacionRequest request
    ) {
        return ResponseEntity.ok(service.actualizar(request, id));
    }

    @Override
    @Operation(
            summary = "Eliminar una habitación",
            description = "Realiza una eliminación lógica. "
                    + "Una habitación OCUPADA no puede eliminarse."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Habitación eliminada."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El ID no es positivo.",
                    content = @Content(
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe una habitación activa con ese ID.",
                    content = @Content(
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "La habitación está ocupada.",
                    content = @Content(
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(
                    description = "ID de la habitación",
                    example = "1",
                    required = true
            )
            @PathVariable Long id
    ) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Consultar una habitación por ID (ruta alternativa)",
            description = "Aplica las mismas reglas que GET /{id}: "
                    + "solo devuelve registros ACTIVO."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Habitación encontrada.",
                    content = @Content(
                            schema = @Schema(implementation = HabitacionResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El ID no es positivo.",
                    content = @Content(
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe una habitación activa con ese ID.",
                    content = @Content(
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            )
    })
    @GetMapping("/id-habitacion/{id}")
    public ResponseEntity<HabitacionResponse> obtenerPorIdHabitacion(
            @Parameter(
                    description = "ID de la habitación",
                    example = "1",
                    required = true
            )
            @PathVariable
            @Positive(message = "El ID de la habitación debe ser positivo")
            Long id
    ) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(
            summary = "Cambiar el estado operativo",
            description = "Códigos: 1 = DISPONIBLE, 2 = OCUPADA, "
                    + "3 = LIMPIEZA y 4 = MANTENIMIENTO. "
                    + "Una habitación OCUPADA no puede cambiarse manualmente "
                    + "a DISPONIBLE; ese cambio corresponde al check-out o cancelación."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado actualizado.",
                    content = @Content(
                            schema = @Schema(implementation = HabitacionResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Alguno de los identificadores no es positivo.",
                    content = @Content(
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "La habitación o el código de estado no existen.",
                    content = @Content(
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Cambio manual de OCUPADA a DISPONIBLE no permitido.",
                    content = @Content(
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            )
    })
    @PutMapping("/{id}/estado/{idEstado}")
    public ResponseEntity<HabitacionResponse> cambiarEstado(
            @Parameter(
                    description = "ID de la habitación",
                    example = "1",
                    required = true
            )
            @PathVariable
            @Positive(message = "El ID de la habitación debe ser positivo")
            Long id,

            @Parameter(
                    description = "Código: 1 DISPONIBLE, 2 OCUPADA, 3 LIMPIEZA, 4 MANTENIMIENTO",
                    example = "3",
                    required = true
            )
            @PathVariable
            @Positive(message = "El código del estado debe ser positivo")
            Long idEstado
    ) {
        return ResponseEntity.ok(service.cambiarEstado(id, idEstado));
    }
}
