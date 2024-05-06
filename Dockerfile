FROM gradle:8-jdk21 AS build

USER gradle
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY src/ ./src

RUN gradle installDist --no-daemon

FROM eclipse-temurin:21-jre-jammy

EXPOSE 8080

WORKDIR /app

COPY --from=build /app/build/install/kotlin-ktor-exposed-starter .

ENTRYPOINT ["./bin/kotlin-ktor-exposed-starter"]