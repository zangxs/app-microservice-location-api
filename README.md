# app-microservice-location-producer

Microservicio principal del sistema de paisajes. Recibe la imagen y metadata de un paisaje, la sube a MinIO/S3, guarda en base de datos y publica un evento en RabbitMQ para moderación.

## Tecnologías

- Java 17
- Spring Boot 3.2.5
- Spring WebFlux
- Spring Security + JWT
- R2DBC + PostgreSQL
- RabbitMQ
- AWS SDK S3 (compatible con MinIO)
- metadata-extractor (EXIF GPS)

## Requisitos previos

- Java 17
- Maven
- PostgreSQL corriendo (ver infraestructura)
- RabbitMQ corriendo (ver infraestructura)
- MinIO corriendo localmente o bucket S3 en AWS
- ngrok u otro túnel para exponer MinIO públicamente (requerido para que Telegram pueda acceder a las imágenes)
- Variables de entorno configuradas
- Token JWT válido generado por `app-microservice-auth`

## Variables de entorno

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `JWT_KEY` | Clave secreta JWT — debe ser la misma que en auth | `MsUEV8IkLNB7...` |
| `DB_LANDSCAPE_HOST` | Host de la base de datos | `localhost` |
| `DB_LANDSCAPE_PORT` | Puerto de la base de datos | `5433` |
| `DB_LANDSCAPE_NAME` | Nombre de la base de datos | `landscapedb` |
| `DB_LANDSCAPE_USER` | Usuario de la base de datos | `landscape` |
| `DB_LANDSCAPE_PASSWORD` | Contraseña de la base de datos | `landscape` |
| `RABBITMQ_HOST` | Host de RabbitMQ | `localhost` |
| `RABBITMQ_PORT` | Puerto de RabbitMQ | `5672` |
| `RABBITMQ_USER` | Usuario de RabbitMQ | `guest` |
| `RABBITMQ_PASSWORD` | Contraseña de RabbitMQ | `guest` |
| `MINIO_URL` | URL pública de MinIO o endpoint de S3 | `https://abc.ngrok-free.dev` |
| `MINIO_ACCESS_KEY` | Access key de MinIO/S3 | `minioadmin` |
| `MINIO_SECRET_KEY` | Secret key de MinIO/S3 | `minioadmin` |
| `MINIO_BUCKET` | Nombre del bucket | `landscapes` |

## Cómo ejecutar localmente

1. Levantar la infraestructura:
```bash
cd infrastructure
docker-compose up -d
```

2. Exponer MinIO con ngrok (necesario para que Telegram acceda a las imágenes):
```bash
ngrok start minio
```
Copia la URL generada y úsala como `MINIO_URL`.

3. Configurar las variables de entorno en `~/.bashrc`:
```bash
export JWT_KEY=tu_clave_base64
export MINIO_URL=https://tu-url.ngrok-free.dev
export MINIO_ACCESS_KEY=minioadmin
export MINIO_SECRET_KEY=minioadmin
export RABBITMQ_USER=guest
export RABBITMQ_PASSWORD=guest
source ~/.bashrc
```

4. Correr el servicio:
```bash
cd app-microservice-location-producer
mvn spring-boot:run
```

El servicio queda disponible en `http://localhost:8001/app-microservice-location`

## Endpoints

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `POST` | `/app-microservice-location/upload` | Subir imagen y metadata de paisaje | JWT |

### POST /upload

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: multipart/form-data
```

**Form Data:**
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `file` | File | Imagen del paisaje (jpg, png) |
| `title` | String | Título del paisaje |
| `description` | String | Descripción del paisaje |
| `latitude` | String | Latitud en decimal (ej: `7.841722`) |
| `longitude` | String | Longitud en decimal (ej: `-72.641611`) |

> Si la imagen tiene metadata GPS (EXIF), las coordenadas se extraen automáticamente y tienen prioridad sobre las del request.

**Response:**
```json
{
  "dateTime": "2026-03-28T20:00:00",
  "code": 200,
  "data": {
    "landscapeId": "809d3246-7cf2-4dab-b2dc-18fdab63d9c6",
    "status": "PENDING"
  }
}
```

## Flujo interno

```
Request llega con JWT
        ↓
JWTAuthFilter valida el token
        ↓
Extrae coordenadas GPS de la imagen (EXIF)
        ↓
Sube la imagen a MinIO/S3
        ↓
Guarda metadata en PostgreSQL con status PENDING
        ↓
Publica LandscapeEvent en RabbitMQ
        ↓
Escucha LandscapeStatusEvent de RabbitMQ
        ↓
Actualiza status en BD (APPROVED / REJECTED)
```

## Base de datos

El servicio se conecta a `landscapedb` en PostgreSQL:

```sql
CREATE TABLE IF NOT EXISTS landscape (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     BIGINT NOT NULL,
    title       VARCHAR(100) NOT NULL,
    description TEXT,
    latitude    DECIMAL(10, 8) NOT NULL,
    longitude   DECIMAL(11, 8) NOT NULL,
    image_url   VARCHAR(500) NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
);
```

## Colas RabbitMQ

| Cola | Dirección | Descripción |
|------|-----------|-------------|
| `landscape.pending.queue` | Producer → Consumer | Evento de nuevo paisaje para moderar |
| `landscape.status.queue` | Consumer → Producer | Resultado de la moderación |

## Notas

- El JWT debe ser generado por `app-microservice-auth` con la misma `JWT_KEY`
- Las coordenadas van en formato decimal: Norte/Este positivos, Sur/Oeste negativos
- Las imágenes enviadas por WhatsApp o Telegram pierden la metadata EXIF — enviar directamente desde el sistema de archivos
- En producción reemplazar MinIO por AWS S3 cambiando las variables de entorno