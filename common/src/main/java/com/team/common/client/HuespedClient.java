package com.team.common.client;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("huespedes")
public interface HuespedClient {
}
