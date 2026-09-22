package com.team.huespedes.controller;

import com.team.common.controller.CrudController;
import com.team.common.dto.huespedes.HuespedRequest;
import com.team.common.dto.huespedes.HuespedResponse;
import com.team.huespedes.service.HuespedService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Huespedes")
public class HuespedController extends CrudController<HuespedRequest, HuespedResponse, HuespedService> {

    public HuespedController(HuespedService service) {
        super(service);
    }

    @GetMapping("/id-huesped/{id}")
    public HuespedResponse obtenerPorIdHuesped(
            @PathVariable @Positive(message = "El ID debe ser positivo") Long id
    ) {
        return service.obtenerPorIdSinEstado(id);
    }
}
