package com.example.payments.cdm;

import java.util.Objects;

public class DefaultIsoPaymentMapper implements IsoPaymentMapper {

    @Override
    public PaymentInstruction toInstruction(IsoPacs008Payment pacs008Payment) {
        Objects.requireNonNull(pacs008Payment, "pacs008Payment");
        return new PaymentInstruction(
                pacs008Payment.msgId(),
                pacs008Payment.debtorIban(),
                pacs008Payment.creditorIban(),
                pacs008Payment.currency(),
                pacs008Payment.amount(),
                pacs008Payment.requestedExecutionDate(),
                pacs008Payment.purposeCode()
        );
    }
}
