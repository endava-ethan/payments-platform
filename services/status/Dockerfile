# syntax=docker/dockerfile:1.6
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY ../../pom.xml ./
COPY ../../libs ./libs
COPY ../../services/status ./services/status
RUN mvn -pl services/status -am package -DskipTests

FROM eclipse-temurin:21-jre
ENV OTEL_JAVA_AGENT=/otel/otel-javaagent.jar
WORKDIR /app
COPY --from=build /workspace/services/status/target/status-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java -javaagent:${OTEL_JAVA_AGENT} -jar /app/app.jar"]
