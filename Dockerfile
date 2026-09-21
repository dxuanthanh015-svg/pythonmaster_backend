# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/pythonmaster-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 2909
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
