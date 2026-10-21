# ApiCliente - Microservicio de Interfaz de Gestor de Proyectos

Microservicio frontend / cliente web basado en **Spring Boot 3** y **Java 21**, que consume los servicios de `ApiGestor` y proporciona una interfaz web con plantillas Thymeleaf y Bootstrap 5.

---

## 🛠️ Stack Tecnológico

- **Lenguaje:** Java 21 (LTS)
- **Framework:** Spring Boot 3.2.5
- **Gestor de Dependencias:** Apache Maven 3.9+
- **Motor de Vistas:** Thymeleaf
- **Diseño UI:** Bootstrap 5.3.3
- **Pruebas:** Spring Boot Starter Test (JUnit 5, Mockito)

---

## 🚀 Requisitos y Ejecución

### Requisitos previos:
- JDK 21 instalado y configurado en `JAVA_HOME`.
- Apache Maven 3.9+ (o usar el wrapper `./mvnw`).

### Compilar y empaquetar:
```bash
mvn clean package -DskipTests
```

### Ejecutar el microservicio:
```bash
mvn spring-boot:run
```
Acceso a la interfaz web: [http://localhost:8080](http://localhost:8080) (o el puerto configurado en `application.properties`).

---

## 🔄 Actualización a Java 21

Se realizó el upgrade de **Java 17** a **Java 21 LTS** actualizando la propiedad `<java.version>21</java.version>` en el archivo `pom.xml`, manteniendo total compatibilidad con Spring Boot 3.2.5.
