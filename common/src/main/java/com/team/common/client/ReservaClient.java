package com.team.common.client;


import com.team.common.dto.habitaciones.HabitacionResponse;
import com.team.common.dto.huespedes.HuespedResponse;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.List;

@FeignClient("reservas")
public interface ReservaClient {


}
