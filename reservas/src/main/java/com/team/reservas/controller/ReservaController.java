package com.team.reservas.controller;

import com.team.common.client.HuespedClient;
import com.team.common.dto.habitaciones.HabitacionResponse;
import com.team.common.dto.huespedes.HuespedResponse;
import com.team.reservas.service.ReservaServiceImp;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping
@AllArgsConstructor
@Tag(name = "test")
public class ReservaController {

    private final ReservaServiceImp reservaServiceImp;



    @GetMapping("/huespedes")
    ResponseEntity<List<HuespedResponse>> listarHuespedes()
    {
        return ResponseEntity.status(HttpStatus.OK).body(reservaServiceImp.listarHuespedes());
    }

    @GetMapping("/habitaciones")
    ResponseEntity<List<HabitacionResponse>> listarHabitaciones()
    {
        return ResponseEntity.status(HttpStatus.OK).body(reservaServiceImp.listarHabitaciones());
    }



}
