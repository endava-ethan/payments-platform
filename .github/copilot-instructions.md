# Copilot Instructions for payments-platform

## Project Overview
- **Domain:** Secure ISO 20022-compliant payments platform using Java 21, Spring Boot 3, and microservices.
- **Major Services:**
  - `services/ingress`: ISO XML validation, persistence, mapping to CDM, Kafka publish.
  - `services/orchestrator`: Consumes CDM events, orchestrates payment logic, emits status.
  - `services/egress`: Converts CDM to ISO pacs.002 XML for downstream.
  - `services/status`: HTTP API for payment status queries.
  - `libs/`: Shared validation, CDM, ISO XSD assets.

## Key Architectural Patterns
- **Immutable ISO XML:** Original XML is never mutated; metadata is carried in JSON headers.
- **Versioned Topics:** Kafka topics are versioned (e.g., `iso20022.inbound.pacs.008.v10`).
- **Validation Pipeline:** XSD → Semantic rules → Authorization/context → Persistence → Publish.
- **Idempotency:** Enforced via MongoDB compound index on `{messageName, messageVersion, messageId, correlationId}`.
- **Deterministic Mapping:** XML ↔ JSON mapping is covered by golden tests.

## Developer Workflows
- **Build:** `mvn clean verify` (run from project root)
- **Run Services:** Use Docker Compose (`ops/docker/docker-compose.yml`) for dependencies. Start services with Java 21 and OpenTelemetry agent.
- **Integration Tests:** Use `mvn verify -Pintegration` (Testcontainers for Kafka/Mongo, profile TBD).
- **Contract Tests:** Defined by AsyncAPI/JSON schema in `schemas/asyncapi/payments-messaging.yaml` and `schemas/cdm/payment-instruction.schema.json`.
- **Security:**
  - mTLS for Kafka (Spring config)
  - MongoDB CSFLE (Spring config)
  - XML parsing hardened (see `IsoXmlParser`)
  - Sensitive data encrypted in GridFS
- **Observability:** Mount OpenTelemetry Java agent at `$OTEL_JAVA_AGENT` for all services. OTLP traces/metrics/logs exported to `otel-collector`.
- **CI:** GitHub Actions (`.github/workflows/ci.yml`) runs build, SBOM, dependency, and container CVE checks.

## Project-Specific Conventions
- **No plaintext logging of sensitive XML.**
- **All message schemas and topics are defined in `schemas/asyncapi/` and `schemas/cdm/`.**
- **Rule packs for validation in `rule-packs/src/main/resources/rules/`.**
- **Sample ISO messages in `fixtures/`.**

## References
- `README.md`: High-level architecture, runbook, and security notes.
- `ops/docker/docker-compose.yml`: Local dev dependencies and observability stack.
- `services/*/src/`: Service-specific logic and entrypoints.
- `libs/`: Shared code and validation logic.

---

**If any section is unclear or missing, please provide feedback for further refinement.**
