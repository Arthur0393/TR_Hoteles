# Proyecto Final

## 🏨 Sistema de Gestión de Reservas Hoteleras
## Proyecto Fullstack con Spring Boot, Microservicios y Angular

## 🎯 Objetivo del Proyecto
Diseñar e implementar desde cero un sistema FULLSTACK distribuido para la gestión de:
• Usuarios
• Huéspedes
• Habitaciones
• Reservas Hoteleras

El proyecto debe aplicar obligatoriamente:
• Arquitectura de microservicios
• Frontend en Angular
• Validaciones estrictas en frontend y backend
• Reglas de negocio obligatorias
• Manejo correcto de estados y transiciones
• Integridad entre microservicios
• Manejo adecuado de errores HTTP

📌 Este proyecto evalúa diseño, criterio técnico y lógica de negocio.
📌 No se proporciona infraestructura, seguridad, frontend ni código base.

---

## 🌐 Arquitectura General (OBLIGATORIA)
El alumno debe diseñar e implementar completamente la siguiente arquitectura:

### Backend
• Spring Boot
• Arquitectura de microservicios
• Eureka Server
• API Gateway
• Feign Clients
• Seguridad basada en JWT

### Frontend
• Angular
• Servicios HTTP
• Guards por rol
• Interceptor JWT
• Manejo de errores HTTP

📌 Restricciones importantes:
• El Authorization Server NO es un microservicio del sistema. Debe implementarse como componente independiente.
• Los microservicios NO manejan autenticación directamente.
• Toda petición del frontend DEBE pasar por el API Gateway.
• El frontend NO contiene lógica de negocio, solo validaciones y control de flujo.

---

## 👥 Roles del Sistema

### USER (Recepcionista)
Puede:
• Registrar y consultar huéspedes
• Consultar habitaciones
• Crear reservas
• Modificar reservas (según reglas)
• Realizar check-in
• Realizar check-out

No puede:
• Crear o eliminar usuarios
• Modificar precios de habitaciones
• Eliminar habitaciones
• Acceder a reportes globales

### ADMIN (Gerente)
Puede:
• Todo lo que hace USER
• Crear, modificar y eliminar usuarios
• Crear, modificar y eliminar habitaciones
• Modificar precios
• Cambiar estados de habitaciones
• Consultar reportes

📌 Las restricciones de rol deben cumplirse en frontend (guards) y backend.

---

## 📌 Estados y Catálogos Oficiales

### 📋 Estado del Registro (EstadoRegistro)
• ACTIVO
• ELIMINADO

📌 Prohibido eliminar registros físicamente.

### 🏨 Estado de la Habitación (EstadoHabitacion)
| Código | Estado | Descripción |
| --- | --- | --- |
| 1 | DISPONIBLE | Lista para asignarse |
| 2 | OCUPADA | Asignada a una reserva |
| 3 | LIMPIEZA | En limpieza |
| 4 | MANTENIMIENTO | En reparación |

### 📅 Estado de la Reserva (EstadoReserva)
| Código | Estado | Descripción |
| --- | --- | --- |
| 1 | CONFIRMADA | Reserva creada |
| 2 | EN_CURSO | Check-in realizado |
| 3 | FINALIZADA | Check-out realizado |
| 4 | CANCELADA | Reserva cancelada |

---

## 🔐 Regla General de Unicidad (CRÍTICA)
Los siguientes campos deben ser únicos SOLO entre registros ACTIVO.

• Huéspedes: Email, Teléfono, Documento
• Habitaciones: Número de habitación
• Usuarios: Username

📛 No se permite duplicar valores únicos en POST, PUT ni PATCH si existe otro registro ACTIVO con el mismo valor.

---

## 👤 Módulo Usuarios

### 📍 Endpoints mínimos
| Método | Endpoint |
| --- | --- |
| POST | / |
| GET | / |
| GET | /{id} |
| PUT | /{id} |
| DELETE | /{id} |

### 📋 Reglas de Negocio – Usuario
• Username: 5 a 20 caracteres, único entre ACTIVOS.
• Password: Mínimo 8 caracteres, letras y números, almacenada encriptada.
• Rol: ADMIN o USER (Obligatorio).

📌 Restricciones:
• Solo ADMIN puede gestionar usuarios.
• Usuarios ELIMINADOS no pueden autenticarse.

📌 Frontend obligatorio:
• Validaciones de longitud y formato.
• Guards que impidan acceso a USER.
• Manejo de errores 401, 403 y 409.

---

## 👤 Módulo Huéspedes

### 📍 Endpoints mínimos
| Método | Endpoint |
| --- | --- |
| POST | / |
| GET | / |
| GET | /{id} |
| GET | /id-huesped/{id} |
| PUT | /{id} |
| DELETE | /{id} |

### 📋 Reglas de Negocio – Huésped
• Nombre y apellido: Obligatorios, 2 a 50 caracteres.
• Email: Formato válido, único entre ACTIVOS.
• Teléfono: Exactamente 10 dígitos, único entre ACTIVOS.
• Documento: Obligatorio, único entre ACTIVOS.
• Nacionalidad: Obligatoria.

📌 Restricción: No se puede eliminar un huésped con reservas EN_CURSO.
📌 Validaciones deben existir en frontend y backend.

---

## 🏨 Módulo Habitaciones

### 📍 Endpoints mínimos
| Método | Endpoint |
| --- | --- |
| POST | / |
| GET | / |
| GET | /{id} |
| GET | /id-habitacion/{id} |
| PUT | /{id} |
| PUT | /{id}/estado/{idEstado} |
| DELETE | /{id} |

### 📋 Reglas de Negocio – Habitación
• Número: Entero > 0, único entre ACTIVOS.
• Tipo: Obligatorio.
• Precio: Mayor a 0.
• Capacidad: Mínimo 1.
• Estado inicial: DISPONIBLE.

📌 Restricciones:
• ❌ No se puede eliminar una habitación OCUPADA.
• ❌ No se puede cambiar manualmente a DISPONIBLE si está OCUPADA.

---

## 📅 Módulo Reservas

### 📍 Endpoints mínimos
| Método | Endpoint |
| --- | --- |
| POST | / |
| GET | / |
| GET | /{id} |
| PUT | /{id} |
| PATCH | /{idReserva}/estado/{idEstado} |
| DELETE | /{id} |

### 📋 Reglas de Negocio – Reserva
• El huésped debe existir y estar ACTIVO.
• La habitación debe existir, estar ACTIVA y DISPONIBLE.
• Estado inicial: CONFIRMADA.

📌 No se manejan fechas ni horas reales. Las fechas son representativas para efectos de validación.

---

## 🔗 Regla Crítica de Integridad: Reserva ↔ Habitación

### Al crear una reserva
• La habitación DEBE estar DISPONIBLE.
• La reserva se registra en estado CONFIRMADA.
• La habitación cambia automáticamente a OCUPADA.

### Check-in
• Reserva pasa a EN_CURSO.
• La habitación permanece OCUPADA.

### Check-out
• Reserva pasa a FINALIZADA.
• La habitación cambia automáticamente a DISPONIBLE.

### Cancelación
• Solo permitida en estado CONFIRMADA.
• La habitación cambia automáticamente a DISPONIBLE.

---

## 🔁 Reglas de Modificación de Reservas (OBLIGATORIAS)
📌 La modificación depende exclusivamente de si el check-in ya fue realizado.

### 🔹 Reserva SIN check-in (CONFIRMADA)
Se permite modificar:
• Fecha de entrada
• Fecha de salida

📛 Siempre debe cumplirse: fechaEntrada < fechaSalida

### 🔹 Reserva CON check-in (EN_CURSO)
Se permite modificar:
• ÚNICAMENTE la fecha de salida

📛 Restricciones:
• ❌ No se puede modificar fecha de entrada
• ❌ No se puede cancelar
• ❌ No se puede cambiar habitación

### 🔹 Reservas FINALIZADAS o CANCELADAS
• ❌ No se permite ninguna modificación
• Solo consulta histórica

---

## 🔁 Validaciones Frontend + Backend (OBLIGATORIAS)
📌 Regla crítica del proyecto: Toda validación debe existir en backend y TODA validación de campos debe existir también en frontend.
📛 Validar solo en frontend o solo en backend NO es suficiente.

---

## ⚠️ Manejo de Errores (OBLIGATORIO)

| Situación | Código HTTP |
| --- | --- |
| Validación incorrecta | 400 |
| Recurso no encontrado | 404 |
| Violación de regla de negocio | 409 |
| No autorizado | 401 |
| Prohibido | 403 |
| Error interno | 500 |

📌 El frontend debe mostrar mensajes claros según el código HTTP.

---

## 📊 Rúbrica de Evaluación

### 🧱 1. Arquitectura y Diseño (15%)
• Microservicios bien separados
• API Gateway obligatorio
• Eureka funcional
• Authorization Server independiente

### 🎨 2. Frontend Angular (20%)
• Uso correcto de Angular
• Formularios reactivos
• Validaciones completas
• Guards por rol
• Interceptor JWT

### 📋 3. Reglas de Negocio Backend (25%)
• Estados correctos
• Transiciones válidas
• Integridad Reserva ↔ Habitación
• Unicidad entre ACTIVOS

### 🧪 4. Validaciones y Errores (20%)
• Validaciones en frontend y backend
• Uso correcto de HTTP 400, 404, 409
• Mensajes claros

### 🧼 5. Calidad del Código (20%)
• Capas bien definidas
• Sin lógica en controllers
• Código reutilizable
• Manejo correcto de excepciones

---

## 🧪 Casos de Prueba Obligatorios

### Reservas
• Crear reserva con habitación DISPONIBLE → OK
• Crear reserva con habitación OCUPADA → 409
• Check-in → habitación sigue OCUPADA
• Check-out → habitación DISPONIBLE
• Cancelar CONFIRMADA → OK
• Cancelar EN_CURSO → 409
• Modificar fechas antes de check-in → OK
• Modificar fecha de entrada después de check-in → 409
• Modificar fecha de salida después de check-in → OK

### Habitaciones
• Eliminar DISPONIBLE → OK
• Eliminar OCUPADA → 409

### Huéspedes
• Email duplicado ACTIVO → 409
• Email duplicado ELIMINADO → OK

### Usuarios
• Crear usuario como ADMIN → OK
• Crear usuario como USER → 403

📌 Todos deben funcionar desde frontend y backend.

---

## ❌ Errores Graves (INVALIDAN EL PROYECTO)

### Frontend
• No usar Angular
• No usar formularios reactivos
• Ignorar roles
• Permitir acciones inválidas

### Backend
• No usar API Gateway
• No usar Eureka
• Authorization Server como microservicio
• Ignorar reglas de negocio
• Permitir cambiar habitación

---

## 🧠 Nota Final
Este proyecto no se evalúa por apariencia ni solo por funcionar. Se evalúa:
• Pensamiento de dominio
• Respeto absoluto a las reglas
• Control de estados
• Integridad del sistema

📌 Una regla violada invalida el flujo completo.
📌 En sistemas reales, la lógica de negocio no se negocia.
