package com.example.payments.validation;

@FunctionalInterface
public interface ValidationStep<T> {
    ValidationResult validate(T target, ValidationContext context);
}
