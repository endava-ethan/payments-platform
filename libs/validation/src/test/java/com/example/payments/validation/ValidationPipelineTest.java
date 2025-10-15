package com.example.payments.validation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationPipelineTest {

    @Test
    void shouldAccumulateErrors() {
        ValidationPipeline<String> pipeline = new ValidationPipeline<>();
        pipeline.registerStep((value, ctx) -> value.startsWith("ISO") ? ValidationResult.ok()
                : ValidationResult.failed(List.of("must start with ISO")));
        pipeline.registerStep((value, ctx) -> value.endsWith("v10") ? ValidationResult.ok()
                : ValidationResult.failed(List.of("must end with version")));

        ValidationResult result = pipeline.validate("pacs.008", ValidationContext.empty());

        assertThat(result.valid()).isFalse();
        assertThat(result.errors()).containsExactlyInAnyOrder("must start with ISO", "must end with version");
    }
}
