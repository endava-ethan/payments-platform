package com.example.payments.ingress.validation;

import com.example.payments.cdm.PaymentInstruction;
import com.example.payments.validation.ValidationResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SemanticPaymentValidator {

    public ValidationResult validate(PaymentInstruction instruction) {
        List<String> errors = new ArrayList<>();
        if (!"EUR".equals(instruction.currency())) {
            errors.add("Only EUR currency is supported in pilot");
        }
        if (instruction.amount() == null || instruction.amount().signum() <= 0) {
            errors.add("Amount must be positive");
        }
        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.failed(errors);
    }
}
