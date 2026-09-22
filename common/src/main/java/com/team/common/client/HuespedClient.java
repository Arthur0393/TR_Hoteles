package com.team.common.client;


import com.team.common.dto.huespedes.HuespedResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("huespedes")
public interface HuespedClient {

    @GetMapping()
    List <HuespedResponse> listar();

    @GetMapping("/id-huesped/{id}")
    HuespedResponse obtenerPorIdActivo(@PathVariable("id") Long id);
}
