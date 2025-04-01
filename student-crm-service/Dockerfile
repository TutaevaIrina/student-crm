# Build stage
FROM openjdk:21-slim AS build
WORKDIR /app
COPY . .
RUN ./gradlew build

# Run stage
FROM openjdk:21-slim
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]