package com.team.reservas.service;


import com.team.common.client.HabitacionClient;
import com.team.common.client.HuespedClient;
import com.team.common.dto.habitaciones.HabitacionResponse;
import com.team.common.dto.huespedes.HuespedResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReservaServiceImp {
    private final HuespedClient huespedClient;

    private final HabitacionClient habitacionClient;

    public List<HuespedResponse> listarHuespedes ()
    {
        return huespedClient.listar();
    }

    public List<HabitacionResponse> listarHabitaciones ()
    {
        return habitacionClient.listar();
    }



}
