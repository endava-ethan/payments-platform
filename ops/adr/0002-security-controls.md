# ADR 0002: Security Controls

## Context

Payment flows contain PII and financial data. Regulators require encryption in transit, hardened XML parsing, and auditable storage of originals.

## Decision

- Enforce mutual TLS on all broker connections (Kafka, IBM MQ, RabbitMQ) using Spring Boot SSL properties.
- Harden XML parsing with `DocumentBuilderFactory` security features (disable DTDs, entity expansion) and compute SHA-256 hashes for immutability checks.
- Store raw ISO XML in MongoDB GridFS with Client-Side Field Level Encryption (CSFLE). Persist only hashed identifiers outside encrypted fields.
- Disallow raw XML logging. Only structured metadata is logged; status propagation reuses Kafka headers.

## Consequences

- Runtime deployments must distribute truststores/keystores and CSFLE key vault metadata.
- Additional monitoring ensures TLS certificate rotation does not break connectivity.
- Developers must use provided helpers (`IsoXmlParser`, `GridFsStorage`) instead of bespoke parsing/storage logic.
