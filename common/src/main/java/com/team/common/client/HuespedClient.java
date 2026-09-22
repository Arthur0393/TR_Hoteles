package com.team.common.client;


import com.team.common.dto.habitaciones.HabitacionResponse;
import com.team.common.dto.huespedes.HuespedResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("huespedes")
public interface HuespedClient {

    @GetMapping()
    List <HuespedResponse> listar();
}
