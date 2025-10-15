package com.example.payments.ingress.config;

import com.example.payments.cdm.PaymentInstruction;
import com.example.payments.ingress.validation.DocumentSchemaValidator;
import com.example.payments.ingress.validation.SemanticPaymentValidator;
import com.example.payments.validation.ValidationPipeline;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.w3c.dom.Document;

@Configuration
public class IngressValidationConfig {

    @Bean
    public ValidationPipeline<Document> schemaValidationPipeline(DocumentSchemaValidator schemaValidator) {
        return new ValidationPipeline<Document>()
                .registerStep((document, context) -> schemaValidator.validate(document));
    }

    @Bean
    public ValidationPipeline<PaymentInstruction> semanticValidationPipeline(SemanticPaymentValidator validator) {
        return new ValidationPipeline<PaymentInstruction>()
                .registerStep((instruction, context) -> validator.validate(instruction));
    }
}
