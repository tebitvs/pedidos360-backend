# Pedidos360 - Backend BFF

Backend For Frontend (BFF) del sistema **Pedidos360**, desarrollado con Spring Boot para la Evaluación Parcial N°1 de la asignatura **Desarrollo Cloud Native I**.

Este componente recibe las solicitudes provenientes del frontend Angular a través de AWS API Gateway, valida los tokens JWT emitidos por Microsoft Entra ID y posteriormente consume el microservicio interno de pedidos.

## Arquitectura

```text
Angular
   |
   | JWT
   v
Microsoft Entra ID
   |
   v
AWS API Gateway
   |
   v
Spring Boot BFF :8080
   |
   | HTTP interno
   v
Pedidos Service :8081
   |
   v
Amazon RDS MySQL
```

## Tecnologías utilizadas

- Java 21
- Spring Boot 4.1.1
- Spring Security
- OAuth 2.0 Resource Server
- JWT
- Microsoft Entra ID
- AWS EC2
- AWS API Gateway
- REST
- Maven

## Función del BFF

El BFF actúa como punto de entrada al backend de Pedidos360.

Sus responsabilidades principales son:

- Recibir solicitudes provenientes del frontend.
- Validar el JWT enviado por Angular.
- Verificar la firma digital del token.
- Validar el emisor (`iss`).
- Validar la audiencia (`aud`).
- Validar la vigencia del token.
- Comprobar el scope `access_as_user`.
- Rechazar solicitudes sin autenticación.
- Consumir el microservicio interno de pedidos.
- Entregar una respuesta adaptada al frontend.

## Seguridad JWT

El backend utiliza Spring Security como OAuth2 Resource Server.

Se valida el emisor:

```text
https://sts.windows.net/bb5324af-c266-41ed-b36c-a971641c7af2/
```

La audiencia esperada es:

```text
api://770fe373-4625-41b9-831c-50a3d7fd4274
```

El endpoint protegido requiere la autoridad:

```text
SCOPE_access_as_user
```

Las claves públicas utilizadas para verificar la firma del JWT son obtenidas desde Microsoft Entra ID.

## Endpoints

### Estado público

```http
GET /api/public/status
```

No requiere autenticación.

Ejemplo de respuesta:

```json
{
  "mensaje": "Pedidos360 BFF operativo",
  "protegido": false
}
```

### Consulta protegida de pedidos

```http
GET /api/pedidos
```

Requiere:

```text
Authorization: Bearer <JWT>
```

Sin un token válido, el backend responde:

```text
HTTP 401 Unauthorized
```

Con un JWT válido, el BFF consulta el microservicio de pedidos y devuelve los registros al frontend.

## Comunicación con el microservicio

El BFF no consulta directamente Amazon RDS.

Consume el servicio de pedidos mediante:

```text
http://127.0.0.1:8081/internal/pedidos
```

La dirección puede configurarse mediante la variable de entorno:

```text
PEDIDOS_SERVICE_URL
```

Valor utilizado en EC2:

```text
http://127.0.0.1:8081
```

## Despliegue

El BFF está desplegado en una instancia Amazon EC2 y funciona en:

```text
Puerto 8080
```

Se ejecuta como un servicio de Linux mediante `systemd`.

Servicio:

```text
pedidos360.service
```

Esto permite que el backend se inicie automáticamente y permanezca activo.

## AWS API Gateway

El frontend no consume directamente la instancia EC2.

Las solicitudes pasan mediante AWS API Gateway:

```text
https://qzm8hwzv31.execute-api.us-east-1.amazonaws.com
```

Endpoint utilizado:

```text
GET /api/pedidos
```

## Ejecución local

Compilar:

```bash
./mvnw clean package
```

Ejecutar:

```bash
./mvnw spring-boot:run
```

El servicio se inicia por defecto en:

```text
http://localhost:8080
```

Para utilizar el microservicio de pedidos se puede definir:

```text
PEDIDOS_SERVICE_URL=http://localhost:8081
```

## Seguridad del repositorio

Este repositorio no contiene:

- Contraseñas de RDS.
- Claves privadas.
- Archivos `.pem`.
- Secretos de AWS.
- Tokens JWT.
- Credenciales de Microsoft Entra ID que requieran secreto.

El archivo `.gitignore` excluye archivos sensibles y artefactos de compilación.

## Repositorios relacionados

Frontend Angular:

```text
https://github.com/tebitvs/pedidos360-frontend
```

Microservicio de pedidos:

```text
https://github.com/tebitvs/pedidos360-pedidos-service
```

## Proyecto académico

**Sistema:** Pedidos360  
**Asignatura:** Desarrollo Cloud Native I  
**Sección:** I_005V  
**Institución:** Duoc UC