package com.team.common.client;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("habitaciones")
public interface HabitacionClient {
}
