FROM openjdk:17
COPY ../micro_Cliente/target/micro_Cliente-0.0.1-SNAPSHOT*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]