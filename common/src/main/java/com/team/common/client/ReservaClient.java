package com.team.common.client;


import com.team.common.configuration.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "reservas",configuration = FeignClientConfig.class)
public interface ReservaClient {

    @GetMapping("/id-huesped/{idHuesped}/en-curso")
    void tieneReservasEnCurso(@PathVariable("idHuesped") Long idHuesped);
}
