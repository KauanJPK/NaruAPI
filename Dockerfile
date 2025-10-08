FROM gradle:8.8-jdk21-jammy AS build
WORKDIR /app
COPY . .
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/NaruAPI.jar
EXPOSE 8080
CMD ["java", "-jar", "NaruAPI.jar"]
