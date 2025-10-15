package com.example.payments.validation;

import java.util.Map;

public record ValidationContext(Map<String, Object> attributes) {
    public static ValidationContext empty() {
        return new ValidationContext(Map.of());
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }
}
