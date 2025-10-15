# syntax=docker/dockerfile:1.6
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY ../../pom.xml ./
COPY ../../libs ./libs
COPY ../../services/egress ./services/egress
RUN mvn -pl services/egress -am package -DskipTests

FROM eclipse-temurin:21-jre
ENV OTEL_JAVA_AGENT=/otel/otel-javaagent.jar
WORKDIR /app
COPY --from=build /workspace/services/egress/target/egress-service-*.jar app.jar
ENTRYPOINT ["sh","-c","java -javaagent:${OTEL_JAVA_AGENT} -jar /app/app.jar"]
