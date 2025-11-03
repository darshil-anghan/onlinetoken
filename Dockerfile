# Use OpenJDK 17 base image
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 3013

ENTRYPOINT ["java", "-jar", "app.jar"]