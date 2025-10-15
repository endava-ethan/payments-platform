package com.example.payments.cdm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentInstruction(
        @NotBlank String instructionId,
        @NotBlank String debtorIban,
        @NotBlank String creditorIban,
        @NotBlank String currency,
        @Positive BigDecimal amount,
        @NotNull LocalDate requestedExecutionDate,
        String purposeCode
) { }
