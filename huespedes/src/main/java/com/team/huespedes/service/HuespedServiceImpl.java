package com.team.huespedes.service;

import com.team.common.dto.huespedes.HuespedRequest;
import com.team.common.dto.huespedes.HuespedResponse;
import com.team.common.enums.Documentacion;
import com.team.common.enums.EstadoRegistro;
import com.team.common.exceptions.RecursoNoEncontradoException;
import com.team.common.utils.StringCustomUtils;
import com.team.common.utils.ValoresNumerico;
import com.team.huespedes.entities.Huesped;
import com.team.huespedes.mapper.HuespedMapper;
import com.team.huespedes.repository.HuespedRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class HuespedServiceImpl implements HuespedService {

    private final HuespedRepository huespedRepository;

    private final HuespedMapper huespedMapper;

    private static final String MENSAJE_DUPLICADO = "Ya existe un huesped ACTIVO con este %s";

    @Transactional(readOnly = true)
    @Override
    public List<HuespedResponse> listar() {

        log.info("Listando todos los huespedes en estado activo");

        return huespedRepository.findAllByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(huespedMapper::entidadAResponse).toList();

    }

    @Transactional(readOnly = true)
    @Override
    public HuespedResponse obtenerPorId(Long id) {
        return huespedMapper.entidadAResponse(obtenerHuespedActivo(id));
    }

    @Override
    public HuespedResponse registrar(HuespedRequest request) {

        Huesped huesped = huespedMapper.requestAEntidad(request);

        validarUnicidad(huesped, null);

        log.info("Registrando huesped con email {}/ {}", huesped.getEmail(), huesped.getIdHuesped());

        return huespedMapper.entidadAResponse(huespedRepository.save(huesped));
    }

    @Override
    public HuespedResponse actualizar(HuespedRequest request, Long id) {

        Huesped huesped = obtenerHuespedActivo(id);

        Huesped datos = huespedMapper.requestAEntidad(request);

        validarUnicidad(datos, id);

        huesped.actualizar(
                datos.getNombre(),
                datos.getApellidoPaterno(),
                datos.getApellidoMaterno(),
                datos.getEmail(),
                datos.getTelefono(),
                datos.getDocumento(),
                datos.getNumDocumento(),
                datos.getNacionalidad(),
                huesped.getEstadoRegistro()
        );

        return huespedMapper.entidadAResponse(huespedRepository.save(huesped));
    }

    @Override
    public void eliminar(Long id) {
        // Pendiente: validar contra reservas EN_CURSO una vez exista ese microservicio
        Huesped huesped = obtenerHuespedActivo(id);

        log.info("Eliminando logicamente el huesped con id {}", id);

        huesped.actualizar(
                huesped.getNombre(),
                huesped.getApellidoPaterno(),
                huesped.getApellidoMaterno(),
                huesped.getEmail(),
                huesped.getTelefono(),
                huesped.getDocumento(),
                huesped.getNumDocumento(),
                huesped.getNacionalidad(),
                EstadoRegistro.ELIMINADO
        );

        huespedRepository.save(huesped);
    }

    @Transactional(readOnly = true)
    @Override
    public HuespedResponse obtenerPorIdSinEstado(Long id) {
        return huespedMapper.entidadAResponse(obtenerHuespedSinEstado(id));
    }

    private Huesped obtenerHuespedActivo(Long id)
    {
        log.info("Obteniendo huesped activo con id {}", id);

        ValoresNumerico.validarNumeroRequerido(id);

        return huespedRepository.findByIdHuespedAndEstadoRegistro(id,EstadoRegistro.ACTIVO).orElseThrow(
                ()-> new RecursoNoEncontradoException("NO se ha encontrado el huesped activo con id "+id)
        );
    }

    private Huesped obtenerHuespedSinEstado(Long id)
    {
        log.info("Obteniendo huesped sin estado  con id {}", id);

        ValoresNumerico.validarNumeroRequerido(id);

        return huespedRepository.findById(id).orElseThrow(
                ()-> new RecursoNoEncontradoException("NO se ha encontrado el huesped con id "+id)
        );
    }

    private void validarUnicidad(Huesped huesped, Long idActual) {

        validarEmail(huesped.getEmail(), idActual);

        validarTelefono(huesped.getTelefono(), idActual);

        validarDocumento(huesped.getDocumento(), huesped.getNumDocumento(), idActual);
    }

    private void validarEmail(String email, Long idActual) {
        StringCustomUtils.validarNoVacio(email,"El email del huesped es requerido");

        Optional<Huesped> huespedDuplicado = huespedRepository.findByEmailIgnoreCaseAndEstadoRegistro(email, EstadoRegistro.ACTIVO);

        if (huespedDuplicado.isPresent() && esOtroRegistro(huespedDuplicado.get(), idActual)) {
            throw new IllegalStateException(String.format(MENSAJE_DUPLICADO, "email"));
        }
    }

    private void validarTelefono(String telefono, Long idActual) {
        StringCustomUtils.validarNoVacio(telefono, "El telefono del huesped es requerido");

        Optional<Huesped> huespedDuplicado = huespedRepository.findByTelefonoAndEstadoRegistro(telefono, EstadoRegistro.ACTIVO);

        if (huespedDuplicado.isPresent() && esOtroRegistro(huespedDuplicado.get(), idActual)) {
            throw new IllegalStateException(String.format(MENSAJE_DUPLICADO, "telefono"));
        }
    }

    private void validarDocumento(Documentacion documento, String numDocumento, Long idActual) {
        Optional<Huesped> huespedDuplicado = huespedRepository.findByDocumentoAndNumDocumentoAndEstadoRegistro(documento, numDocumento, EstadoRegistro.ACTIVO);

        if (huespedDuplicado.isPresent() && esOtroRegistro(huespedDuplicado.get(), idActual)) {
            throw new IllegalStateException(MENSAJE_DUPLICADO.formatted("documento"));
        }
    }
    private boolean esOtroRegistro(Huesped huesped, Long idActual) {
        return idActual == null || !huesped.getIdHuesped().equals(idActual);
    }
}
