package com.example.payments.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationPipeline<T> {

    private final List<ValidationStep<T>> steps = new ArrayList<>();

    public ValidationPipeline<T> registerStep(ValidationStep<T> step) {
        steps.add(step);
        return this;
    }

    public ValidationResult validate(T target, ValidationContext context) {
        List<String> errors = new ArrayList<>();
        for (ValidationStep<T> step : steps) {
            ValidationResult result = step.validate(target, context);
            if (!result.valid()) {
                errors.addAll(result.errors());
            }
        }
        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.failed(errors);
    }
}
