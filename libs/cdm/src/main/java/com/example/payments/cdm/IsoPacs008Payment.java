package com.example.payments.cdm;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IsoPacs008Payment(
        String msgId,
        String debtorIban,
        String creditorIban,
        String currency,
        BigDecimal amount,
        LocalDate requestedExecutionDate,
        String purposeCode
) { }
