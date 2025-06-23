# 🏗️ Stage 1: Build with Maven + Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy only the necessary files first for caching
COPY pom.xml .
COPY src ./src

# Build the application (skip tests to speed up)
RUN mvn clean package -DskipTests

# 🚀 Stage 2: Run with Temurin JDK 21
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/uddco-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
