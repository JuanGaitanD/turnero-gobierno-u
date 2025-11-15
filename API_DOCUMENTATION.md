# API de Gestión de Turnos - Gobierno

Sistema de gestión de turnos con prioridades automáticas para atención al ciudadano.

## 🎯 Sistema de Prioridades

El sistema asigna prioridades automáticamente según estas reglas:

1. **Prioridad 3 (Máxima)**: 
   - Personas mayores de 60 años (inmediata)
   - PQRS con más de 10 minutos de espera (dinámica)

2. **Prioridad 2 (Media)**: 
   - PQRS recientes (menos de 10 minutos)

3. **Prioridad 1 (Normal)**: 
   - Trámite de vivienda
   - Atención tributaria

## 📋 Endpoints de la API

### Base URL
```
http://localhost:8080/api/turnos
```

### 1. Crear un nuevo turno
**POST** `/api/turnos`

**Request Body:**
```json
{
  "usuarioId": 1,
  "servicio": "pqrs"
}
```

**Servicios disponibles:**
- `tramite_vivienda`
- `atencion_tributaria`
- `pqrs`

**Response:** `201 CREATED`
```json
{
  "id": 1,
  "numeroTurno": "T0001",
  "usuarioNombre": "Juan Pérez",
  "usuarioDni": "12345678",
  "usuarioEdad": 65,
  "servicio": "pqrs",
  "estado": "PENDIENTE",
  "prioridad": 3,
  "fechaCreacion": "2025-11-14T10:30:00",
  "fechaAtencion": null
}
```

### 2. Obtener todos los turnos
**GET** `/api/turnos`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "numeroTurno": "T0001",
    "usuarioNombre": "Juan Pérez",
    "usuarioDni": "12345678",
    "usuarioEdad": 65,
    "servicio": "pqrs",
    "estado": "PENDIENTE",
    "prioridad": 3,
    "fechaCreacion": "2025-11-14T10:30:00",
    "fechaAtencion": null
  }
]
```

### 3. Obtener turnos pendientes
**GET** `/api/turnos/pendientes`

Retorna todos los turnos con estado `PENDIENTE`, ordenados por prioridad (descendente) y fecha de creación (ascendente).

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "numeroTurno": "T0001",
    "prioridad": 3,
    "estado": "PENDIENTE",
    ...
  }
]
```

### 4. Obtener siguiente turno
**GET** `/api/turnos/siguiente`

Obtiene el siguiente turno a atender según prioridad. Este endpoint:
- Actualiza automáticamente las prioridades de PQRS con más de 10 minutos
- Selecciona el turno con mayor prioridad
- Cambia su estado a `EN_ATENCION`
- Registra la fecha de atención

**Response:** `200 OK`
```json
{
  "id": 1,
  "numeroTurno": "T0001",
  "usuarioNombre": "Juan Pérez",
  "usuarioDni": "12345678",
  "usuarioEdad": 65,
  "servicio": "pqrs",
  "estado": "EN_ATENCION",
  "prioridad": 3,
  "fechaCreacion": "2025-11-14T10:30:00",
  "fechaAtencion": "2025-11-14T10:35:00"
}
```

**Response:** `204 NO CONTENT` (cuando no hay turnos pendientes)

### 5. Finalizar un turno
**PUT** `/api/turnos/{id}/finalizar`

Marca un turno como `ATENDIDO`.

**Response:** `200 OK`
```json
{
  "id": 1,
  "numeroTurno": "T0001",
  "estado": "ATENDIDO",
  ...
}
```

**Response:** `404 NOT FOUND` (turno no encontrado)

### 6. Cancelar un turno
**PUT** `/api/turnos/{id}/cancelar`

Marca un turno como `CANCELADO`.

**Response:** `200 OK`
```json
{
  "id": 1,
  "numeroTurno": "T0001",
  "estado": "CANCELADO",
  ...
}
```

## 🔄 Estados del Turno

- **PENDIENTE**: Turno creado, esperando atención
- **EN_ATENCION**: Turno siendo atendido actualmente
- **ATENDIDO**: Turno finalizado exitosamente
- **CANCELADO**: Turno cancelado

## 💡 Ejemplos de Uso

### Crear turno para persona mayor de 60 años
```bash
curl -X POST http://localhost:8080/api/turnos \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "servicio": "tramite_vivienda"
  }'
```
→ Se asignará **Prioridad 3** automáticamente

### Crear turno PQRS
```bash
curl -X POST http://localhost:8080/api/turnos \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 2,
    "servicio": "pqrs"
  }'
```
→ Se asignará **Prioridad 2** inicialmente
→ Después de 10 minutos, se elevará automáticamente a **Prioridad 3**

### Obtener siguiente turno
```bash
curl http://localhost:8080/api/turnos/siguiente
```

### Ver turnos pendientes
```bash
curl http://localhost:8080/api/turnos/pendientes
```

## 🗄️ Estructura de la Base de Datos

### Tabla: turnos
```sql
- id (BIGINT, PK, AUTO_INCREMENT)
- id_usuario (BIGINT, FK a usuarios)
- numeroTurno (VARCHAR, UNIQUE)
- fechaCreacion (DATETIME)
- servicio (ENUM: tramite_vivienda, atencion_tributaria, pqrs)
- estado (ENUM: PENDIENTE, EN_ATENCION, ATENDIDO, CANCELADO)
- prioridad (INTEGER: 1, 2, 3)
- fechaAtencion (DATETIME, NULLABLE)
```

### Tabla: usuarios
```sql
- id_usuario (BIGINT, PK, AUTO_INCREMENT)
- dni (VARCHAR)
- nombre_completo (VARCHAR)
- genero (ENUM: H, M)
- fecha_nacimiento (DATE)
- edad (INTEGER)
```

## 🚀 Cómo Ejecutar

1. Asegúrate de tener configurada la base de datos en `application.properties`
2. Ejecuta la aplicación Spring Boot:
   ```bash
   mvn spring-boot:run
   ```
3. La API estará disponible en `http://localhost:8080`

## 🧪 Pruebas

Para probar el sistema de prioridades:

1. Crea un usuario mayor de 60 años
2. Crea varios turnos con diferentes servicios
3. Crea turnos PQRS
4. Espera 10+ minutos
5. Llama al endpoint `/siguiente` y observa cómo se actualizan las prioridades

## 📦 Dependencias Requeridas

- Spring Boot 3.5.7
- Spring Data JPA
- Lombok
- MySQL/MariaDB Driver
