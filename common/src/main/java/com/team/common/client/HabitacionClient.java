package com.team.common.client;


import com.team.common.dto.habitaciones.HabitacionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient("habitaciones")
public interface HabitacionClient {

    @GetMapping
    List<HabitacionResponse> listar();

    @GetMapping("/id-habitacion/{id}")
    HabitacionResponse obtenerPorId(@PathVariable("id") Long id);

    @PutMapping("/{id}/ocupar")
    void ocupar(@PathVariable("id") Long id);

    @PutMapping("/{id}/liberar")
    void liberar(@PathVariable("id") Long id);
}
