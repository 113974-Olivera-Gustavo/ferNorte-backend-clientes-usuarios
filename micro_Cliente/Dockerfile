# Usa una imagen base con OpenJDK 17
FROM openjdk:17

# Establece el directorio de trabajo
WORKDIR /app

# Copia el JAR desde la ruta absoluta a la imagen
COPY "C:/Users/Darwoft/Desktop/Nueva carpeta/ferNorte-backend-clientes-usuarios/micro_Cliente/target/micro_Cliente-0.0.1-SNAPSHOT.jar" app.jar

# Comando de entrada para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
