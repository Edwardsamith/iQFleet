# Glosario — iQFleet

Definición de términos del dominio de negocio y del sistema.

---

## Términos del dominio

**Flota**
Conjunto de vehículos de transporte público que pertenecen a un mismo propietario y operan bajo una misma administración.

**Conductor**
Persona que opera un vehículo de la flota. Debe poseer licencia de conducción vigente en la categoría habilitante para el tipo de vehículo asignado.

**Propietario**
Persona natural o jurídica dueña de uno o más vehículos de transporte público. Es el actor con máxima autoridad dentro del sistema.

**SOAT**
Seguro Obligatorio de Accidentes de Tránsito. Documento legal de renovación anual obligatoria en Colombia para la circulación de vehículos.

**RTM / Revisión Técnico-Mecánica**
Certificado que acredita que un vehículo cumple con las condiciones técnicas y de emisiones exigidas por la normativa colombiana. Su periodicidad varía según el año del vehículo.

**Tarjeta de Operación**
Documento expedido por la autoridad de transporte que habilita a un vehículo para prestar servicio público en una ruta o zona determinada.

**Tarjeta de Propiedad**
Documento oficial que certifica quién es el propietario de un vehículo. No tiene fecha de vencimiento pero debe actualizarse ante cambios de dueño.

**Comparendo**
Sanción de tránsito impuesta a un conductor o vehículo por infracciones a las normas de tránsito. Acumular comparendos puede inhabilitar la operación.

**Paz y Salvo de Tránsito**
Certificado que acredita que un conductor o vehículo no tiene comparendos pendientes de pago ante las autoridades de tránsito.

**Recaudo diario**
Ingreso económico generado por la operación de un vehículo en ruta durante un día hábil.

**Mantenimiento preventivo**
Revisión o reparación programada realizada con base en el kilometraje o el tiempo transcurrido, para prevenir fallas.

**Mantenimiento correctivo**
Reparación no programada realizada como respuesta a una falla o daño detectado en el vehículo.

---

## Términos del sistema

**API REST**
Interfaz de programación de aplicaciones que sigue los principios REST (Representational State Transfer). Permite que clientes externos consuman la funcionalidad del sistema mediante peticiones HTTP.

**JWT (JSON Web Token)**
Estándar de token de acceso seguro. iQFleet lo usa para autenticar usuarios: al iniciar sesión el servidor emite un JWT firmado que el cliente incluye en cada petición subsiguiente.

**CQRS**
Command Query Responsibility Segregation. Patrón de diseño que separa las operaciones que modifican el estado del sistema (Commands) de las que solo lo consultan (Queries).

**Command**
Objeto que representa una intención de modificar el estado del sistema (crear, actualizar, eliminar). Implementa `ICommand<TResponse>`.

**Query**
Objeto que representa una solicitud de datos sin efecto de lado. Implementa `IQuery<TResponse>`.

**Handler**
Clase que implementa la lógica de procesamiento de un Command o Query específico. Cada Command y cada Query tiene exactamente un Handler.

**Mediator**
Bus de mensajes que recibe un Command o Query y lo despacha al Handler correspondiente. Desacopla el emisor del procesador.

**Result\<T\>**
Tipo de retorno de todos los Handlers. Encapsula el éxito (con valor opcional) o el fracaso (con lista de mensajes de error), evitando el uso de excepciones como control de flujo.

**Clean Architecture**
Estilo arquitectural que organiza el código en capas concéntricas (Domain, Application, Infrastructure, Presentation) donde la dirección de dependencia siempre apunta hacia adentro.

**Entidad**
Objeto del dominio con identidad única persistida en base de datos. En iQFleet: `Conductor`, `Vehiculo`, `Documento`, `MovimientoFinanciero`, `Usuario`.

**Repository**
Interfaz definida en la capa Domain que abstrae el acceso a los datos de una entidad. La implementación concreta vive en Infrastructure.

**Estado de documento**
Clasificación automática calculada por el sistema según la fecha de vencimiento: `VIGENTE` (más de 30 días restantes), `POR_VENCER` (entre 0 y 30 días), `VENCIDO` (fecha superada), `SIN_VENCIMIENTO` (documento sin fecha de expiración).

**Balance**
Diferencia entre los ingresos totales y los egresos totales de un vehículo o de la flota en un período determinado. Balance positivo indica rentabilidad.

**ROLE_OWNER**
Rol de mayor privilegio en el sistema. Asignado al propietario. Tiene acceso completo a todas las funcionalidades, incluida la gestión de usuarios.

**ROLE_ADMIN**
Rol operativo asignado a administradores delegados. Puede gestionar conductores, vehículos, documentos y finanzas, pero no puede crear ni desactivar usuarios.

**BCrypt**
Algoritmo de hashing de contraseñas utilizado por Spring Security. Las contraseñas nunca se almacenan en texto plano; solo su hash BCrypt persiste en la base de datos.

**OpenAPI / Swagger**
Especificación estándar para describir APIs REST. iQFleet usa SpringDoc para generar esta documentación automáticamente desde el código y exponerla en `/swagger-ui.html`.
