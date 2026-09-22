package com.team.habitaciones.repositories;

import com.team.habitaciones.entity.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitacionRepository
        extends JpaRepository<Habitacion, Long> {

    /*
     * Devuelve todas las habitaciones que tengan el estado de registro indicado.
     * Se usara principalmente con "ACTIVO" para que el listado normal no muestre
     * habitaciones eliminadas logicamente.
     *
     * Spring Data construye la consulta a partir del nombre del metodo:
     * findAllBy + EstadoRegistro equivale a buscar por el atributo estadoRegistro
     * que existe dentro de la entidad Habitacion.
     */
    List<Habitacion> findAllByEstadoRegistro(
            String estadoRegistro
    );

    /*
     * Busca una habitacion por su identificador y, al mismo tiempo, comprueba que
     * tenga el estado de registro solicitado. Devuelve Optional porque puede no
     * existir el ID o porque la habitacion puede estar marcada como ELIMINADO.
     *
     * Al consultarlo con "ACTIVO", permite tratar una habitacion eliminada como
     * un recurso que ya no esta disponible para las operaciones normales.
     */
    Optional<Habitacion> findByIdAndEstadoRegistro(
            Long id,
            String estadoRegistro
    );

    /*
     * Indica si ya existe una habitacion con el mismo numero y estado de registro.
     * Se usara antes de registrar una habitacion nueva para cumplir la regla de que
     * el numero solo puede repetirse cuando el registro anterior esta ELIMINADO.
     *
     * Retorna true si encuentra al menos una coincidencia y false si no existe.
     */
    boolean existsByNumeroHabitacionAndEstadoRegistro(
            Integer numeroHabitacion,
            String estadoRegistro
    );

    /*
     * Realiza la misma comprobacion de numero duplicado, pero ignora el ID indicado.
     * Es necesaria durante una actualizacion: la habitacion que se esta editando
     * ya posee su propio numero y no debe confundirse con un registro duplicado.
     *
     * Por ejemplo, al actualizar la habitacion con ID 5, busca otra habitacion
     * ACTIVA con el mismo numero cuyo ID sea diferente de 5.
     */
    boolean existsByNumeroHabitacionAndEstadoRegistroAndIdNot(
            Integer numeroHabitacion,
            String estadoRegistro,
            Long id
    );

    /*
     * Comprueba si una habitacion especifica tiene cierto estado operativo y sigue
     * siendo un registro activo. Se puede usar antes de eliminar para verificar si
     * la habitacion esta OCUPADA, caso en el que el README prohibe eliminarla.
     *
     * Los dos estados son distintos: estadoHabitacion describe si esta DISPONIBLE,
     * OCUPADA, etc.; estadoRegistro indica si el registro esta ACTIVO o ELIMINADO.
     */
    boolean existsByIdAndEstadoHabitacionAndEstadoRegistro(
            Long id,
            String estadoHabitacion,
            String estadoRegistro
    );
}
