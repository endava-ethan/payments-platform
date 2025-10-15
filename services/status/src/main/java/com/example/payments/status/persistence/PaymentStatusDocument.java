package com.example.payments.status.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "payment_status")
public class PaymentStatusDocument {

    @Id
    private String id;
    private String instructionId;
    private String status;
    private Instant updatedAt;
    private String reasonCode;

    public PaymentStatusDocument(String instructionId, String status, Instant updatedAt, String reasonCode) {
        this.instructionId = instructionId;
        this.status = status;
        this.updatedAt = updatedAt;
        this.reasonCode = reasonCode;
    }

    public String getInstructionId() {
        return instructionId;
    }

    public String getStatus() {
        return status;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getReasonCode() {
        return reasonCode;
    }
}
