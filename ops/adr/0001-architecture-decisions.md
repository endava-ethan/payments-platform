# ADR 0001: Core Architecture

## Context

We require a modular ISO 20022 payment pipeline with strong validation, security, and observability controls. The platform must preserve ISO XML at the edges, apply deterministic mappings to a canonical JSON model, and operate over a message-driven backbone (Kafka with options for IBM MQ/RabbitMQ).

## Decision

- Adopt **Spring Boot 3 / Java 21** for all services to leverage native virtual threads, record classes, and the Spring Observability stack.
- Structure the build as a Maven multi-module project splitting shared libraries (`libs`) from services.
- Use **Kafka** as the default transport, with Spring Profiles prepared for IBM MQ/RabbitMQ adapters.
- Persist canonical data in **MongoDB** with GridFS for original ISO XML. Mandate CSFLE for PII encryption.
- Enforce validation order (XSD → semantic → auth/context) using the `validation` library.
- Use **MapStruct** for deterministic XML ↔ CDM mappings with golden tests.
- Ship each service as an independent Docker image with multi-stage builds and OpenTelemetry Java agent support.

## Consequences

- Shared libraries promote reuse but require careful version management via the parent POM.
- Kafka-first approach simplifies local development; IBM MQ/RabbitMQ support will rely on Spring profile-specific `@Configuration` classes.
- MongoDB CSFLE requires key vault integration in deployment environments (documented in runbook).
- MapStruct code generation requires annotation processing during builds; Maven configuration ensures processors are on the classpath.
- OpenTelemetry agent must be mounted in runtime environments; container entrypoints expose `OTEL_JAVA_AGENT` variable.
