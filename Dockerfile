# syntax=docker/dockerfile:1
# Uso:
#   docker build --target rest -t personapp-rest .
#   docker build --target cli  -t personapp-cli  .

FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /build

# Capa de dependencias (se cachea mientras no cambien los pom)
COPY pom.xml .
COPY common/pom.xml common/
COPY domain/pom.xml domain/
COPY application/pom.xml application/
COPY maria-output-adapter/pom.xml maria-output-adapter/
COPY mongo-output-adapter/pom.xml mongo-output-adapter/
COPY rest-input-adapter/pom.xml rest-input-adapter/
COPY cli-input-adapter/pom.xml cli-input-adapter/
RUN --mount=type=cache,target=/root/.m2 mvn -B -q dependency:go-offline -DskipTests || true

COPY . .
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests -pl rest-input-adapter,cli-input-adapter -am package

FROM eclipse-temurin:11-jre AS base
RUN useradd --system --create-home --home-dir /app app
WORKDIR /app
RUN mkdir -p /app/logs && chown -R app:app /app
USER app
ENTRYPOINT ["java","-jar","/app/app.jar"]

FROM base AS rest
COPY --from=build --chown=app:app /build/rest-input-adapter/target/rest-input-adapter-*-SNAPSHOT.jar /app/app.jar
EXPOSE 3000

FROM base AS cli
COPY --from=build --chown=app:app /build/cli-input-adapter/target/cli-input-adapter-*-SNAPSHOT.jar /app/app.jar
