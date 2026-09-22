package com.team.common.client;


import com.team.common.dto.habitaciones.HabitacionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("habitaciones")
public interface HabitacionClient {

    @GetMapping
    List<HabitacionResponse> listar();
}
