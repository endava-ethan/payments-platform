package com.example.payments.validation;

import java.util.List;

public record ValidationResult(boolean valid, List<String> errors) {
    public static ValidationResult ok() {
        return new ValidationResult(true, List.of());
    }

    public static ValidationResult failed(List<String> errors) {
        return new ValidationResult(false, List.copyOf(errors));
    }
}
