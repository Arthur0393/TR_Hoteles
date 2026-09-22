package com.team.habitaciones.entity;

import com.team.common.enums.EstadoHabitacion;
import com.team.common.enums.EstadoRegistro;
import com.team.common.utils.StringCustomUtils;
import com.team.common.utils.ValoresNumerico;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * Representa una habitación del hotel y reúne sus datos y reglas de actualización.
 * JPA relaciona cada instancia persistida con una fila de la tabla HABITACIONES.
 */
@Entity // Permite que JPA administre y persista esta clase.
@Table(name = "HABITACIONES")
@AllArgsConstructor // Lombok genera un constructor que recibe todos los atributos.
@NoArgsConstructor // JPA necesita un constructor sin argumentos para crear la entidad.
@Builder // Permite construir habitaciones indicando los atributos por su nombre.
@Getter // Genera métodos de lectura sin exponer setters para cada atributo.

public class Habitacion {

    // Identificador interno: distingue cada registro; la base de datos genera su valor.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "ID_HABITACION")
    private Long id;

    // Número con el que se identifica la habitación en el hotel, separado del ID interno.
    // @Column vincula el atributo con su columna; nullable = false exige un valor en la BD.
    @Column(name = "NUMERO",nullable = false)
    private Integer numeroHabitacion;

    // Describe la categoría de la habitación; el mapeo limita el texto a 50 caracteres.
    @Column(name = "TIPO", length = 50, nullable = false)
    private String tipoHabitacion;

    // Importe de la habitación: BigDecimal permite representar decimales exactos,
    // evitando los errores de representación binaria de float o double para dinero.
    @Column(name= "PRECIO", nullable = false)
    private BigDecimal precio;

    // Cantidad de personas que admite la habitación, necesaria para conocer su capacidad.
    @Column (name = "CAPACIDAD", nullable = false)
    private Integer capacidad;

    // Estado operativo de la habitación, por ejemplo disponible u ocupada.
    // Se separa del estado del registro porque describen situaciones distintas.
    @Column(name = "ESTADO_HABITACION", nullable = false)
    private String estadoHabitacion;

    // Estado lógico del registro, como ACTIVO o ELIMINADO, almacenado como texto.
    // Permite marcar una eliminación conservando la fila en la base de datos.
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private String estadoRegistro;


    /**
     * Revisa los valores recibidos antes de asignarlos para evitar actualizaciones
     * con números nulos o no positivos y textos vacíos o fuera del tamaño permitido.
     * Es privado porque forma parte de las comprobaciones internas de la entidad.
     * Solo se ejecuta al invocarlo: los constructores y el builder no lo llaman.
     * Los estados se validan como texto; aquí no se comprueba que pertenezcan a un enum.
     *
     * @throws IllegalArgumentException si alguno de los valores no cumple las reglas
     */
    private void validarDatos(
            Integer numeroHabitacion,
            String tipoHabitacion,
            BigDecimal precio,
            Integer capacidad,
            String estadoHabitacion,
            String estadoRegistro
    ){
        // Reutiliza la utilidad común para exigir un número de habitación mayor que cero.
        ValoresNumerico.validarNumeroPositivo(
                numeroHabitacion,
                "El numero de habitacion es requerido y debe ser positivo"
            );

        // Exige una categoría no vacía que respete la longitud de la columna TIPO.
        StringCustomUtils.validarTamanio(
                tipoHabitacion,
                1,
                50,
                "El tipo de habitacion es requerido"
        );

        // El precio debe existir y ser mayor que cero según esta regla de negocio.
        ValoresNumerico.validarNumeroPositivo(
                precio,
                "El precio es requerido y debe ser positivo"
        );
        // Una habitación debe admitir al menos una persona.
        ValoresNumerico.validarNumeroPositivo(
                capacidad,
                "La capacidad de la habitacion es requerida y debe ser positiva"
        );
        // Exige un estado operativo informado y de hasta 15 caracteres.
        StringCustomUtils.validarTamanio(
                estadoHabitacion,
                1,
                15,
                "El estado de la habitacion es requerido"
        );
        // Aplica el mismo requisito de texto al estado lógico del registro.
        StringCustomUtils.validarTamanio(
                estadoRegistro,
                1,
                15,
                "El estado del registro es requerida"
                );
    }
    /**
     * Impide actualizar una habitación eliminada o volver a eliminarla.
     * Centraliza esta comprobación para que ambos métodos apliquen la misma regla.
     *
     * @throws IllegalArgumentException si el registro ya está marcado como ELIMINADO
     */
    private void validarNoEliminado(){

        // name() obtiene el texto del enum para compararlo con el String almacenado.
        // Invocar equals sobre ese texto evita un error si estadoRegistro es null.
        if(EstadoRegistro.ELIMINADO.name().equals(this.estadoRegistro)){
            throw new IllegalArgumentException(
                    "La habitacion ya esta eliminada"
            );
        }
    }
    /**
     * Realiza una eliminación lógica: cambia el estado sin borrar físicamente la fila.
     * Conserva los datos para mantener el historial; este método modifica la entidad,
     * mientras que la persistencia del cambio depende de JPA y de la transacción.
     */
    public void eliminar() {
        validarNoEliminado();

        this.estadoRegistro = EstadoRegistro.ELIMINADO.name();
    }

    /**
     * Cambia el estado operativo de una habitacion activa.
     * Una habitacion OCUPADA no se puede liberar manualmente; ese cambio debe ser
     * consecuencia del check-out o de la cancelacion de una reserva.
     */
    public void cambiarEstado(EstadoHabitacion nuevoEstado) {
        validarNoEliminado();

        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado de la habitacion es requerido");
        }

        boolean estaOcupada = EstadoHabitacion.OCUPADA.name().equals(this.estadoHabitacion);
        boolean seQuiereLiberar = EstadoHabitacion.DISPONIBLE.equals(nuevoEstado);

        if (estaOcupada && seQuiereLiberar) {
            throw new IllegalStateException(
                    "No se puede cambiar manualmente una habitacion ocupada a disponible"
            );
        }

        this.estadoHabitacion = nuevoEstado.name();
    }

    /**
     * Ocupa una habitacion activa cuando una reserva la asigna.
     * Solo es posible desde DISPONIBLE; el resto de casos se rechaza con 409.
     */
    public void ocupar() {
        validarNoEliminado();

        if (!EstadoHabitacion.DISPONIBLE.name().equals(this.estadoHabitacion)) {
            throw new IllegalStateException(
                    "La habitacion " + this.id + " no esta disponible, su estado actual es "
                            + this.estadoHabitacion
            );
        }

        this.estadoHabitacion = EstadoHabitacion.OCUPADA.name();
    }

    /**
     * Libera una habitacion ocupada tras el check-out o la cancelacion de la reserva.
     * Solo es posible desde OCUPADA; el cambio manual desde otro estado se rechaza.
     */
    public void liberar() {
        validarNoEliminado();

        if (!EstadoHabitacion.OCUPADA.name().equals(this.estadoHabitacion)) {
            throw new IllegalStateException(
                    "No se puede liberar la habitacion " + this.id
                            + " porque su estado actual es " + this.estadoHabitacion
            );
        }

        this.estadoHabitacion = EstadoHabitacion.DISPONIBLE.name();
    }

    /**
     * Sustituye los datos editables de una habitación conservando su identificador.
     * Primero comprueba el estado actual y todos los nuevos valores para evitar
     * modificar parcialmente el objeto si alguna validación falla.
     * La persistencia de los cambios depende de JPA y de la transacción.
     *
     * @throws IllegalArgumentException si está eliminada o los datos son inválidos
     */
    public void  actualizar(
            Integer numeroHabitacion,
            String tipoHabitacion,
            BigDecimal precio,
            Integer capacidad,
            String estadoHabitacion,
            String estadoRegistro
    ) {
        validarNoEliminado();

        validarDatos(
                numeroHabitacion,
                tipoHabitacion,
                precio,
                capacidad,
                estadoHabitacion,
                estadoRegistro
        );
        this.numeroHabitacion = numeroHabitacion;
        this.tipoHabitacion = tipoHabitacion;
        this.precio = precio;
        this.capacidad = capacidad;
        this.estadoHabitacion = estadoHabitacion;
        this.estadoRegistro = estadoRegistro;
    }
}
