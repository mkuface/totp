# syntax=docker/dockerfile:1.7

FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY libs ./libs
COPY src ./src

RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests package

FROM gcr.io/distroless/java17-debian12:nonroot
WORKDIR /app

COPY --from=build /workspace/target/*SNAPSHOT.jar /app/app.jar
COPY --from=build /workspace/libs/libMKtotp.so /usr/lib/libMKtotp.so
COPY --from=build /workspace/libs/libcrypto.so.3 /usr/lib/libcrypto.so.3
COPY --from=build /workspace/src/main/resources/application-docker.yml /app/application.yml

EXPOSE 8010
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
