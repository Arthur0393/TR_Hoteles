package com.team.common.client;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("reservas")
public interface ReservaClient {
}
