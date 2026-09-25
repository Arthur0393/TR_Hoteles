package com.team.common.client;


import com.team.common.configuration.FeignClientConfig;
import com.team.common.dto.huespedes.HuespedResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "huespedes",configuration = FeignClientConfig.class)
public interface HuespedClient {

    @GetMapping()
    List <HuespedResponse> listar();

    // La consulta histórica /id-huesped/{id} también devuelve registros ELIMINADO.
    // Para crear reservas se debe consultar exclusivamente el recurso ACTIVO.
    @GetMapping("/{id}")
    HuespedResponse obtenerPorIdActivo(@PathVariable("id") Long id);
}
