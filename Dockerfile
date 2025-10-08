
FROM gradle:8.8-jdk21-alpine AS build
WORKDIR /app
COPY . .
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
