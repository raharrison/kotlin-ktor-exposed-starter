FROM gradle:7-jdk19 AS build

USER gradle
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY src/ ./src

RUN gradle installDist --no-daemon

FROM eclipse-temurin:19-jre-jammy

EXPOSE 8080

WORKDIR /app

COPY --from=build /app/build/install/kotlin-ktor-exposed-starter .

ENTRYPOINT ["./bin/kotlin-ktor-exposed-starter"]