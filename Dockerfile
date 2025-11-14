# -------- Stage 1: Build & Test --------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven config for caching dependencies
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Pre-download dependencies for faster rebuilds
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# --- Run tests first ---
RUN ./mvnw clean test

# --- Package ONLY if tests pass ---
RUN ./mvnw package -DskipTests

# -------- Stage 2: Run app --------
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
