package com.team.reservas.controller;

import com.team.common.controller.CrudController;
import com.team.common.dto.reservas.ReservaRequest;
import com.team.common.dto.reservas.ReservaResponse;
import com.team.reservas.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Reservas")
public class ReservaController extends CrudController<ReservaRequest, ReservaResponse, ReservaService> {

    public ReservaController(ReservaService service) {
        super(service);
    }

    @Operation(summary = "Cambia el estado de la reserva",
            description = "2=EN_CURSO (check-in), 3=FINALIZADA (check-out), 4=CANCELADA")
    @ApiResponse(responseCode = "409", description = "Transicion no permitida")
    @PatchMapping("/{idReserva}/estado/{idEstado}")
    public ResponseEntity<ReservaResponse> cambiarEstado(
            @PathVariable @Positive(message = "El ID de la reserva debe ser positivo") Long idReserva,
            @PathVariable @Positive(message = "El ID del estado debe ser positivo") Long idEstado
    ) {
        return ResponseEntity.ok(service.cambiarEstado(idReserva, idEstado));
    }

    @Operation(summary = "Indica si el huesped tiene reservas EN_CURSO",
            description = "Uso interno del microservicio de huespedes")
    @GetMapping("/id-huesped/{idHuesped}/en-curso")
    public void tieneReservasEnCurso(
            @PathVariable @Positive(message = "El ID del huesped debe ser positivo") Long idHuesped
    ) {
        service.tieneReservasEnCurso(idHuesped);
    }
}
