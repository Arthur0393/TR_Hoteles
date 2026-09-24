package com.team.habitaciones.service;

import com.team.common.dto.habitaciones.HabitacionRequest;
import com.team.common.dto.habitaciones.HabitacionResponse;
import com.team.common.enums.EstadoHabitacion;
import com.team.common.enums.EstadoRegistro;
import com.team.common.exceptions.RecursoNoEncontradoException;
import com.team.habitaciones.entity.Habitacion;
import com.team.habitaciones.mapper.HabitacionesMapper;
import com.team.habitaciones.repositories.HabitacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Ejecuta los casos de uso y las reglas de negocio de las habitaciones.
 * El repositorio se limita a consultar y guardar; este servicio decide que
 * operaciones son validas antes de modificar la base de datos.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HabitacionServiceImpl implements HabitacionService {

    private final HabitacionRepository habitacionRepository;
    private final HabitacionesMapper habitacionesMapper;

    /**
     * Lista solamente habitaciones activas. La eliminacion es logica, por lo que
     * los registros ELIMINADO permanecen en Oracle pero no se muestran aqui.
     */
    @Override
    @Transactional(readOnly = true)
    public List<HabitacionResponse> listar() {
        log.info("Consultando habitaciones activas");

        return habitacionRepository
                .findAllByEstadoRegistro(EstadoRegistro.ACTIVO)
                .stream()
                .map(habitacionesMapper::entidadAResponse)
                .toList();
    }

    /**
     * Consulta por ID unicamente entre registros activos. Una habitacion eliminada
     * se trata como no disponible y genera la misma respuesta 404 que un ID inexistente.
     */
    @Override
    @Transactional(readOnly = true)
    public HabitacionResponse obtenerPorId(Long id) {
        log.info("Buscando habitacion activa con ID {}", id);
        return habitacionesMapper.entidadAResponse(buscarHabitacionActiva(id));
    }

    /**
     * Crea una habitacion despues de verificar que no exista otra ACTIVA con el
     * mismo numero. El mapper asigna los estados iniciales DISPONIBLE y ACTIVO.
     */
    @Override
    public HabitacionResponse registrar(HabitacionRequest request) {
        log.info("Registrando habitacion numero {}", request.numeroHabitacion());

        validarNumeroParaRegistro(request.numeroHabitacion());

        Habitacion habitacion = habitacionesMapper.requestAEntidad(request);
        Habitacion guardada = habitacionRepository.save(habitacion);

        log.info("Habitacion registrada con ID {}", habitacion.getId());
        return habitacionesMapper.entidadAResponse(guardada);
    }

    /**
     * Modifica los datos editables sin alterar los estados. La comprobacion de
     * numero duplicado ignora el propio ID para permitir conservar su numero actual.
     */
    @Override
    public HabitacionResponse actualizar(HabitacionRequest request, Long id) {
        log.info("Actualizando habitacion con ID {}", id);

        Habitacion habitacion = buscarHabitacionActiva(id);
        validarNumeroParaActualizacion(request.numeroHabitacion(), id);

        habitacion.actualizar(
                request.numeroHabitacion(),
                request.tipoHabitacion().name(),
                request.precio(),
                request.capacidad(),
                habitacion.getEstadoHabitacion(),
                habitacion.getEstadoRegistro()
        );

        Habitacion actualizada = habitacionRepository.save(habitacion);
        log.info("Habitacion con ID {} actualizada", id);

        return habitacionesMapper.entidadAResponse(actualizada);
    }

    /**
     * Marca el registro como ELIMINADO sin borrar la fila. La operacion se rechaza
     * con conflicto 409 cuando la habitacion esta OCUPADA.
     */
    @Override
    public void eliminar(Long id) {
        log.info("Solicitando eliminacion logica de la habitacion con ID {}", id);

        Habitacion habitacion = buscarHabitacionActiva(id);

        if (EstadoHabitacion.OCUPADA.equals(habitacion.getEstadoHabitacion())) {
            throw new IllegalStateException("No se puede eliminar una habitacion ocupada");
        }

        habitacion.eliminar();
        habitacionRepository.save(habitacion);

        log.info("Habitacion con ID {} eliminada logicamente", id);
    }

    /**
     * Convierte el codigo del catalogo a un estado y solicita a la entidad aplicar
     * el cambio. La entidad impide liberar manualmente una habitacion OCUPADA.
     */
    @Override
    public HabitacionResponse cambiarEstado(Long id, Long idEstado) {
        log.info("Cambiando habitacion con ID {} al estado con codigo {}", id, idEstado);

        Habitacion habitacion = buscarHabitacionActiva(id);
        EstadoHabitacion nuevoEstado = EstadoHabitacion.obtenerEstadoPorCodigo(idEstado);

        habitacion.cambiarEstado(nuevoEstado);
        Habitacion actualizada = habitacionRepository.save(habitacion);

        log.info("Habitacion con ID {} cambiada al estado {}", id, nuevoEstado.name());
        return habitacionesMapper.entidadAResponse(actualizada);
    }

    /**
     * Uso interno del microservicio de reservas: ocupa una habitacion DISPONIBLE.
     * Se rechaza con 409 cuando la habitacion no puede pasar a OCUPADA.
     */
    @Transactional
    @Override
    public void ocupar(Long id) {
        log.info("Ocupando habitacion con ID {} por solicitud de reservas", id);

        Habitacion habitacion = buscarHabitacionActiva(id);
        habitacion.ocupar();
        habitacionRepository.save(habitacion);
    }

    /**
     * Uso interno del microservicio de reservas: libera una habitacion OCUPADA.
     * Se rechaza con 409 cuando la habitacion no puede pasar a DISPONIBLE.
     */
    @Transactional
    @Override
    public void liberar(Long id) {
        log.info("Liberando habitacion con ID {} por solicitud de reservas", id);

        Habitacion habitacion = buscarHabitacionActiva(id);
        habitacion.liberar();
        habitacionRepository.save(habitacion);
    }

    /**
     * Reutiliza en varias operaciones la busqueda de una habitacion activa y la
     * construccion del error 404 cuando no se encuentra.
     */
    private Habitacion buscarHabitacionActiva(Long id) {
        return habitacionRepository
                .findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Habitacion con ID " + id + " no encontrada"
                ));
    }

    /** Comprueba la unicidad del numero entre registros activos durante un POST. */
    private void validarNumeroParaRegistro(Integer numeroHabitacion) {
        boolean duplicado = habitacionRepository
                .existsByNumeroHabitacionAndEstadoRegistro(
                        numeroHabitacion,
                        EstadoRegistro.ACTIVO
                );

        if (duplicado) {
            throw new IllegalStateException(
                    "Ya existe una habitacion activa con el numero " + numeroHabitacion
            );
        }
    }

    /**
     * Comprueba la unicidad durante un PUT excluyendo la habitacion que se edita,
     * para que conservar su propio numero no sea interpretado como duplicado.
     */
    private void validarNumeroParaActualizacion(Integer numeroHabitacion, Long id) {
        boolean duplicado = habitacionRepository
                .existsByNumeroHabitacionAndEstadoRegistroAndIdNot(
                        numeroHabitacion,
                        EstadoRegistro.ACTIVO,
                        id
                );

        if (duplicado) {
            throw new IllegalStateException(
                    "Ya existe otra habitacion activa con el numero " + numeroHabitacion
            );
        }
    }
}
