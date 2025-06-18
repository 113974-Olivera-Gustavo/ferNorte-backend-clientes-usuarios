# ferNorte - Backend Clientes y Usuarios

Este repositorio contiene la implementación de los servicios backend utilizados en el **Trabajo Práctico Integrador** (TPI) de la Tecnicatura Universitaria en Programación de la UTN FRC (2023). El proyecto se divide en dos microservicios independientes, desarrollados con **Spring Boot 3** y **Java 17**.

## Microservicios

| Módulo | Puerto | Descripción |
| ------ | ------ | ----------- |
| **micro_Cliente** | `8085` | Gestión de clientes, solicitudes y turnos. Incluye controladores REST y plantillas Thymeleaf. |
| **micro_Usuarios** | `8087` | Autenticación y administración de usuarios internos. Implementa JWT y Spring Security. |

Cada módulo posee su propio `application.properties` donde se define la conexión a PostgreSQL y las rutas de OpenAPI/Swagger.

## Requisitos

- Java 17
- Maven 3
- Docker (opcional, para levantar la base de datos y los servicios con `docker-compose`)

## Compilación

Desde la raíz del repositorio ejecute:

```bash
mvn package
```

Se generarán los JAR de cada microservicio en `micro_Cliente/target/` y `micro_Usuarios/target/`.

## Ejecución

### Con Maven

```bash
# Microservicio de clientes
java -jar micro_Cliente/target/micro_Cliente-0.0.1-SNAPSHOT.jar

# Microservicio de usuarios
java -jar micro_Usuarios/target/micro_Usuarios-0.0.1-SNAPSHOT.jar
```

### Con Docker Compose

El archivo `docker-compose.yml` permite iniciar el microservicio de clientes junto con una base de datos PostgreSQL:

```bash
docker-compose up --build
```

## Documentación

- **Swagger/OpenAPI**: una vez iniciado cada servicio visite `http://localhost:8085/swagger-ui.html` o `http://localhost:8087/swagger-ui.html`.
- **JavaDoc**: se genera en `docs/java_doc` ejecutando `mvn javadoc:javadoc`.
- **Documentación adicional**: diagramas y notas se encuentran en `docs/app_doc`.

---

Este trabajo se realizó como parte de las asignaturas **Laboratorio de Computación IV**, **Programación IV** y **Metodología de Sistemas**.
