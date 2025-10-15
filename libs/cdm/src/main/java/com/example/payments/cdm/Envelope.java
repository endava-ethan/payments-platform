package com.example.payments.cdm;

import java.time.Instant;
import java.util.Objects;

public record Envelope(
        String messageName,
        String messageVersion,
        String schemaUri,
        String correlationId,
        String endToEndId,
        String messageId,
        String sha256,
        Instant receivedAt
) {
    public Envelope {
        Objects.requireNonNull(messageName, "messageName");
        Objects.requireNonNull(messageVersion, "messageVersion");
        Objects.requireNonNull(schemaUri, "schemaUri");
        Objects.requireNonNull(correlationId, "correlationId");
        Objects.requireNonNull(endToEndId, "endToEndId");
        Objects.requireNonNull(sha256, "sha256");
        Objects.requireNonNull(receivedAt, "receivedAt");
    }
}
