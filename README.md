# app-microservice-location-producer
aplicacion encargada de subir una locacion

# Se utiliza MinIO de forma local
docker run -p 9000:9000 -p 9001:9001 \
--name minio \
-v ~/minio/data:/data \
-e "MINIO_ROOT_USER=admin" \
-e "MINIO_ROOT_PASSWORD=password123" \
quay.io/minio/minio server /data --console-address ":9001"

services:
minio:
image: minio/minio
ports:
- "9000:9000"
- "9001:9001"
environment:
MINIO_ROOT_USER: minioadmin
MINIO_ROOT_PASSWORD: minioadmin
command: server /data --console-address ":9001"

conversion
Decimal = Grados + (Minutos / 60) + (Segundos / 3600)
Latitud:  7 + (50/60) + (30.2/3600) = 7.841722   N → positivo
Longitud: 72 + (38/60) + (29.8/3600) = 72.641611  W → negativo