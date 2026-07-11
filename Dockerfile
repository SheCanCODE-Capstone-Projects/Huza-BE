# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Run as a non-root user
RUN groupadd --system huza && useradd --system --gid huza --no-create-home huza

COPY --from=build /workspace/target/*.jar app.jar

USER huza:huza

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
