# Usa una imagen base con OpenJDK 17
FROM openjdk:17

# Establece el directorio de trabajo
WORKDIR /app

# Copia el JAR
COPY "target/micro_Cliente-0.0.1-SNAPSHOT.jar" app.jar

# Comando de entrada para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
