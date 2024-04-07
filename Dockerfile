# Stage 1: Build environment
FROM maven:3.8.3-openjdk-17-slim AS builder
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create a minimal runtime image
FROM openjdk:17-jdk-slim
VOLUME /tmp
ARG JAR_FILE=/workspace/target/*.jar
COPY --from=builder ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
