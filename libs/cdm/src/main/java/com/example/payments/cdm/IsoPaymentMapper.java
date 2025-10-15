package com.example.payments.cdm;

public interface IsoPaymentMapper {
    PaymentInstruction toInstruction(IsoPacs008Payment pacs008Payment);
}
