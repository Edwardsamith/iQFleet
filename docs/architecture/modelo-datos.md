# Modelo de Datos — iQFleet

---

## Diagrama Entidad-Relación

```
┌─────────────────────────┐
│         Usuario          │
├─────────────────────────┤
│ id (PK)                 │
│ nombres                 │
│ apellidos               │
│ tipoIdentificacion      │
│ numeroIdentificacion UK │
│ email UK                │
│ telefono                │
│ nombreUsuario UK        │
│ passwordHash            │
│ rol                     │   ┌──────────────────────────┐
│ estado                  │   │    SolicitudRegistro      │
│ intentosFallidos        │   ├──────────────────────────┤
│ bloqueadoHasta          │◄──┤ id (PK)                  │
│ ultimoAcceso            │   │ usuario (FK)             │
│ fechaCreacion           │   │ estadoDecision           │
└─────────────────────────┘   │ motivoRechazo            │
                               │ revisadoPor (FK Usuario) │
                               │ fechaDecision            │
                               └──────────────────────────┘

┌─────────────────────────┐         ┌───────────────────────────┐
│        Conductor         │         │         Vehiculo           │
├─────────────────────────┤         ├───────────────────────────┤
│ id (PK)                 │    N    │ id (PK)                   │
│ tipoIdentificacion      │◄────────┤ placa UK                  │
│ numeroIdentificacion UK │ asignado│ marca                     │
│ nombres                 │         │ modelo                    │
│ apellidos               │         │ tipoVehiculo              │
│ numeroLicencia UK       │         │ anio                      │
│ categoriaLicencia       │         │ estado                    │
│ vencimientoLicencia     │         │ conductorAsignado (FK)────┤
│ telefono                │         │ responsable               │
│ email UK                │         │ fechaRegistro             │
│ direccion               │         │ fechaCreacion             │
│ estado                  │         └───────────────────────────┘
│ vehiculoAsignado (FK)   │                      │
│ fechaRegistro           │         ┌────────────┴──────────────┐
│ fechaBaja               │         │    HistorialAsignacion     │
│ motivoBaja              │         ├───────────────────────────┤
│ fechaCreacion           │         │ id (PK)                   │
└────────────┬────────────┘         │ vehiculo (FK)             │
             │                       │ conductor (FK)            │
             │1                      │ fechaInicio               │
             │                       │ fechaFin                  │
             │N                      └───────────────────────────┘
┌────────────▼────────────┐
│        Documento         │◄── (también vinculado a Vehiculo)
├─────────────────────────┤
│ id (PK)                 │
│ nombre                  │
│ tipo                    │
│ numeroReferencia        │
│ entidadEmisora          │
│ fechaEmision            │
│ fechaVencimiento        │
│ estado (calculado)      │
│ archivoUrl              │
│ archivoFormato          │
│ observaciones           │
│ conductor (FK nullable) │
│ vehiculo (FK nullable)  │
│ cargadoPor (FK Usuario) │
│ fechaCarga              │
└────────────┬────────────┘
             │1
             │N
┌────────────▼────────────┐
│     VersionDocumento    │
├─────────────────────────┤
│ id (PK)                 │
│ documento (FK)          │
│ archivoUrl              │
│ fechaVencimientoAnterior│
│ reemplazadoPor (FK)     │
│ fechaReemplazo          │
└─────────────────────────┘

┌─────────────────────────┐
│  MovimientoFinanciero   │
├─────────────────────────┤
│ id (PK)                 │
│ tipo                    │
│ categoria               │
│ metodoPago              │
│ valor                   │
│ fecha                   │
│ descripcion             │
│ observaciones           │
│ estado                  │
│ vehiculo (FK)           │──► Vehiculo
│ conductor (FK nullable) │──► Conductor
│ registradoPor (FK)      │──► Usuario
│ fechaCreacion           │
└─────────────────────────┘

┌─────────────────────────┐
│   CodigoRecuperacion    │
├─────────────────────────┤
│ id (PK)                 │
│ usuario (FK)            │──► Usuario
│ codigo                  │
│ medio                   │
│ destinatario            │
│ estado                  │
│ fechaExpiracion         │
│ fechaUso                │
│ fechaCreacion           │
└─────────────────────────┘
```

---

## Descripción de entidades

### Usuario
Representa a cualquier persona con acceso al sistema. Puede ser propietario (`ROLE_OWNER`) o administrador (`ROLE_ADMIN`).

**Estados:** `PENDIENTE` → `ACTIVO` | `RECHAZADO` | `INACTIVO` | `BLOQUEADO`

---

### SolicitudRegistro
Registro de la decisión de aprobación o rechazo de una solicitud de acceso. Vinculado al usuario solicitante y al administrador que tomó la decisión.

---

### Conductor
Persona que opera un vehículo de la flota. Tiene información personal, datos de licencia de conducción y estado laboral.

**Estados:** `ACTIVO` | `INACTIVO`

**Relaciones:**
- Un conductor puede estar asignado a un vehículo a la vez (`Vehiculo.conductorAsignado`)
- Un conductor tiene múltiples `Documento`
- Un conductor puede estar vinculado a múltiples `MovimientoFinanciero`

---

### Vehiculo
Unidad de transporte de la flota. Tiene información técnica, estado operativo y conductor asignado.

**Estados:** `ACTIVO` | `EN_MANTENIMIENTO` | `INACTIVO`

**Relaciones:**
- Un vehículo tiene un conductor asignado actualmente (FK nullable)
- Un vehículo tiene múltiples `Documento`
- Un vehículo tiene múltiples `MovimientoFinanciero`
- Un vehículo tiene un historial de asignaciones (`HistorialAsignacion`)

---

### HistorialAsignacion
Registro histórico de las asignaciones conductor-vehículo. Cuando se cambia la asignación, la anterior queda registrada con su fecha de fin.

---

### Documento
Documento legal o administrativo asociado a un conductor o un vehículo. Puede tener fecha de vencimiento o no. El estado de vigencia se calcula dinámicamente.

**Estados calculados:** `VIGENTE` | `PROXIMO_A_VENCER` | `VENCIDO` | `INACTIVO` | `SIN_VENCIMIENTO`

**Nota de diseño:** `conductor` y `vehiculo` son mutuamente excluyentes. Exactamente uno de los dos debe ser no nulo.

---

### VersionDocumento
Historial de versiones de un documento. Cuando se renueva un documento, la versión anterior queda registrada aquí.

---

### MovimientoFinanciero
Registro de un ingreso o egreso asociado a un vehículo. Puede estar vinculado opcionalmente a un conductor.

**Tipos:** `INGRESO` | `EGRESO`

**Estados:** `ACTIVO` | `ANULADO`

---

### CodigoRecuperacion
Código OTP temporal generado para el proceso de recuperación de contraseña (RF-007).

**Estados:** `PENDIENTE` | `USADO` | `EXPIRADO`

---

## Enumeraciones

### Rol de usuario
```
ROLE_OWNER   - Propietario con acceso total
ROLE_ADMIN   - Administrador con acceso operativo
```

### Estado de usuario
```
PENDIENTE    - Solicitud registrada, sin revisar
ACTIVO       - Cuenta habilitada
RECHAZADO    - Solicitud rechazada
INACTIVO     - Desactivado manualmente
BLOQUEADO    - Bloqueado por intentos fallidos
```

### Tipo de identificación
```
CC    - Cédula de Ciudadanía
CE    - Cédula de Extranjería
PA    - Pasaporte
```

### Categoría de licencia de conducción
```
A1, A2             - Motocicletas
B1, B2, B3         - Vehículos livianos y camiones
C1, C2, C3         - Vehículos de transporte público
```

### Tipo de vehículo
```
BUS        - Bus de transporte público
BUSETA     - Buseta
MICROBUS   - Microbús
VAN        - Van de pasajeros
```

### Estado de vehículo
```
ACTIVO              - Operativo
EN_MANTENIMIENTO    - En taller
INACTIVO            - Fuera de servicio
```

### Tipo de documento
```
-- Vehículo --
SOAT, RTM, TARJETA_OPERACION, TARJETA_PROPIEDAD, POLIZA_RC, OTRO_VEHICULO

-- Conductor --
LICENCIA_CONDUCCION, CERTIFICADO_MEDICO, CONTRATO_VINCULACION,
PAZ_SALVO_TRANSITO, OTRO_CONDUCTOR

-- Administrativo --
HABILITACION_EMPRESA, CONTRATO_RUTA, OTRO_ADMIN
```

### Estado calculado de documento
```
VIGENTE              - Más de diasAlerta días para vencer
PROXIMO_A_VENCER     - Dentro de los próximos diasAlerta días
VENCIDO              - Fecha superada
INACTIVO             - Dado de baja manualmente
SIN_VENCIMIENTO      - Sin fecha de expiración
```

### Tipo de movimiento financiero
```
INGRESO    - Entrada de dinero
EGRESO     - Salida de dinero
```

### Categoría de movimiento financiero
```
-- Ingresos --
RECAUDO, SUBSIDIO, OTRO_INGRESO

-- Egresos --
COMBUSTIBLE, MANT_PREVENTIVO, MANT_CORRECTIVO,
SALARIO, SEGURO, TRAMITE, IMPUESTO, OTRO_EGRESO
```

### Método de pago
```
EFECTIVO         - Pago en efectivo
TRANSFERENCIA    - Transferencia bancaria
```
