FROM gradle:9-jdk25 AS build

USER gradle
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY src/ ./src

RUN gradle installDist --no-daemon

FROM eclipse-temurin:25-jre-noble

EXPOSE 8080

WORKDIR /app

COPY --from=build /app/build/install/kotlin-ktor-exposed-starter .

ENTRYPOINT ["./bin/kotlin-ktor-exposed-starter"]