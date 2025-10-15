package com.example.payments.cdm;

import java.time.Instant;

public record PaymentStatus(
        String instructionId,
        String status,
        Instant updatedAt,
        String reasonCode
) { }
