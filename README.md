# Payments Platform

A secure ISO 20022-compliant microservices reference implementation built with Java 21 and Spring Boot 3. The system ingests pacs.008/pain.001 XML messages, maps them to a canonical JSON data model, orchestrates payment processing, and emits pacs.002 status messages.

## Architecture Overview

- **Ingress Service** (`services/ingress`): Validates ISO XML (XSD + semantic rule packs), persists originals to MongoDB GridFS, maps to Canonical Data Model (CDM), and publishes JSON to Kafka.
- **Orchestrator Service** (`services/orchestrator`): Consumes CDM events, applies orchestration logic, and publishes status updates.
- **Egress Service** (`services/egress`): Converts CDM statuses to ISO pacs.002 XML for downstream clearing networks.
- **Status Service** (`services/status`): Reactive HTTP API for querying payment statuses.
- **Shared Libraries** (`libs/*`): Validation pipeline, CDM definitions, and ISO XSD assets.

Key patterns enforced:

1. Topics are versioned (e.g. `iso20022.inbound.pacs.008.v10`).
2. ISO XML envelopes stay immutable; JSON headers carry metadata (messageName, version, schemaUri, correlationId, endToEndId, sha256).
3. Validation sequence: XSD → Semantic rules → Authorization/context → Persistence → Publish.
4. Idempotency via Mongo compound index on `{messageName, messageVersion, messageId, correlationId}`.
5. Deterministic XML ↔ JSON mapping covered by golden tests.

## Runbook

### Prerequisites

- Docker / Docker Compose
- Java 21 & Maven 3.9+

### Local Development

```bash
# Start dependencies
cd ops/docker
docker compose up -d

# Build all modules
mvn clean verify

# Run ingress service with telemetry agent mounted
OTEL_JAVA_AGENT=./otel-javaagent.jar \
java -javaagent:$OTEL_JAVA_AGENT -jar services/ingress/target/ingress-service-0.1.0-SNAPSHOT.jar
```

Kafka topics are declared in `schemas/asyncapi/payments-messaging.yaml`. Use Testcontainers-backed integration tests (`mvn verify -Pintegration` TBD) for local validation.

### Security Controls

- mTLS to Kafka brokers (configure trust/key stores via Spring properties).
- XML parsing hardened against XXE (`IsoXmlParser`).
- MongoDB Client-Side Field Level Encryption (configure via `spring.data.mongodb.autoEncryptionSettings`).
- Sensitive XML is stored encrypted in GridFS and never logged in plaintext.

### Observability

Each service expects an OpenTelemetry Java agent mounted at `$OTEL_JAVA_AGENT` and exports OTLP traces/metrics/logs to `otel-collector` (see `ops/docker/docker-compose.yml`).

### Testing Strategy

- Unit tests for mapping, validation pipeline, and services.
- Integration tests leverage Testcontainers for Kafka/Mongo (add via Maven profile as needed).
- Contract tests defined by AsyncAPI/JSON schema.

### Continuous Integration

GitHub Actions workflow (`.github/workflows/ci.yml`) executes Maven build, SBOM generation, dependency scanning, and container CVE checks.

### Additional Assets

- AsyncAPI definitions: `schemas/asyncapi/payments-messaging.yaml`
- CDM JSON Schema: `schemas/cdm/payment-instruction.schema.json`
- ISO samples: `fixtures/pacs.008/sample-pacs008.xml`, `fixtures/pain.001/sample-pain001.xml`
- Rule packs: `rule-packs/src/main/resources/rules/ebics-market-practice.yaml`

## Future Work

- Implement semantic rule engine evaluator for YAML expressions.
- Add Testcontainers-based integration suite and contract tests per topic.
- Externalize IBM MQ/RabbitMQ Spring profiles.
- Automate Mongo CSFLE configuration and key management.
