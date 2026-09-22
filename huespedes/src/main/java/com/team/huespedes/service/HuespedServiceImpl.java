package com.team.huespedes.service;

import com.team.common.dto.huespedes.HuespedRequest;
import com.team.common.dto.huespedes.HuespedResponse;
import com.team.common.enums.EstadoRegistro;
import com.team.common.exceptions.RecursoNoEncontradoException;
import com.team.common.utils.ValoresNumerico;
import com.team.huespedes.entities.Huesped;
import com.team.huespedes.mapper.HuespedMapper;
import com.team.huespedes.repository.HuespedRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Slf4j
@Transactional
public class HuespedServiceImpl implements  HuespedService {

    private final HuespedRepository huespedRepository;

    private final HuespedMapper huespedMapper;

    @Transactional(readOnly = true)
    @Override
    public List<HuespedResponse> listar() {

        log.info("Listando todos los huespedes en estado activo");

        return huespedRepository.findAllByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(huespedMapper::entidadAResponse).toList();

    }

    @Override
    public HuespedResponse obtenerPorId(Long id) {
        return huespedMapper.entidadAResponse(obtenerHuespedActivo(id));
    }

    @Override
    public HuespedResponse registrar(HuespedRequest request) {
        return null;
    }

    @Override
    public HuespedResponse actualizar(HuespedRequest request, Long id) {
        return null;
    }

    @Override
    public void eliminar(Long id) {

    }

    @Override
    public HuespedResponse obtenerPorIdSinEstado(Long id) {
        return huespedMapper.entidadAResponse(obtenerHuespedSinEstado(id));
    }

    private Huesped obtenerHuespedActivo(Long id)
    {
        log.info("Obteniendo huesped activo con id "+id );

        ValoresNumerico.validarNumeroRequerido(id);

        return huespedRepository.findByIdHuespedAndEstadoRegistro(id,EstadoRegistro.ACTIVO).orElseThrow(
                ()-> new RecursoNoEncontradoException("NO se ha encontrado el huesped activo con id "+id)
        );


    }

    private Huesped obtenerHuespedSinEstado(Long id)
    {
        log.info("Obteniendo huesped sin estado  con id "+id );

        ValoresNumerico.validarNumeroRequerido(id);

        return huespedRepository.findById(id).orElseThrow(
                ()-> new RecursoNoEncontradoException("NO se ha encontrado el huesped con id "+id)
        );


    }

}
