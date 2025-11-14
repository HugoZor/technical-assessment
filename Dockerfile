# -------- Stage 1: Build jar --------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven config for caching dependencies
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies offline
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the fat jar (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# -------- Stage 2: Run jar --------
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
