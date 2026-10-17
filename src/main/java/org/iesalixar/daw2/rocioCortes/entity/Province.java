package org.iesalixar.daw2.rocioCortes.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * La clase `Province` representa una entidad que modela una provincia dentro de la
 base de datos.
 * Contiene cuatro campos: `id`, `code`, `name` y `id_region`, donde `id` es el identificador
 único de la provincia,
 * `code` es un código asociado a la provincia, `name` es el nombre de la provincia
 * y `id_region` es el codigo asociado a la región a la que pertenece la provincia.
 *
 * Las anotaciones de Lombok ayudan a reducir el código repetitivo al generar
 automáticamente
 * métodos comunes como getters, setters, constructores, y otros métodos estándar
 de los objetos.
 */
@Data// Esta anotación de Lombok genera automáticamente los siguientes métodos:
// - Getters y setters para todos los campos (id, code, name).
// - Los métodos `equals()` y `hashCode()` basados en todos los campos no transitorios.
// - El método `toString()` que incluye todos los campos.
// - Un método `canEqual()` que verifica si una instancia puede ser igual a otra.
// Esto evita tener que escribir manualmente todos estos métodos y mejora la mantenibilidad del código.
@NoArgsConstructor // Esta anotación genera un constructor sin argumentos (constructor vacío),
// es útil cuando quieres crear un objeto `Province` sin inicializar inmediatamente
// con valores. Esto es muy útil en frameworks como Hibernate o JPA,
// que requieren un constructor vacío para la creación de entidades.
@AllArgsConstructor // Esta anotación genera un constructor que acepta todos los campos como parámetros (id, code, name).
// Este constructor es útil cuando necesitas crear una instancia completamente inicializada de `Province`.
        public class Province {
            // Campo que almacena el identificador único de la provincia. Este campo suele ser autogenerado
            // por la base de datos, lo que lo convierte en un buen candidato para una clave primaria.
            private int id;
            // Campo que almacena el código de la región, normalmente una cadena corta que identifica la región.
            // Ejemplo: "02" para Albacete.
            private String code;
            // Campo que almacena el nombre completo de la provincia, como "Albacete".
            private String name;
            // Campo que almacena el identificador único de la region. Este campo es una clave foranea
            // perteneciente a la clase Region.
            private int id_region;
            /**
             * Este es un constructor personalizado que no incluye el campo `id`.
             * Se utiliza para crear instancias de `Province` cuando no es necesario o no
             se conoce el `id` de la provincia
             * (por ejemplo, antes de insertar la provincia en la base de datos, donde el
             `id` es autogenerado).
             * @param code Código de la provincia.
             * @param name Nombre de la provincia.
             * @param id_region
             */

        public Province(String code, String name, int id_region) {
                    this.code = code;
                    this.name = name;
                    this.id_region = id_region;
                }
}