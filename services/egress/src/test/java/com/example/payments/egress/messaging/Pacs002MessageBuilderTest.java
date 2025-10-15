package com.example.payments.egress.messaging;

import com.example.payments.cdm.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class Pacs002MessageBuilderTest {

    @Test
    void shouldBuildMinimalPacs002Document() {
        Pacs002MessageBuilder builder = new Pacs002MessageBuilder();
        PaymentStatus status = new PaymentStatus("ABC123", "RECEIVED", Instant.parse("2024-05-01T10:15:30Z"), "NONE");
        String xml = builder.toXml(status);
        assertThat(xml).contains("ABC123").contains("RECEIVED").contains("2024-05-01T10:15:30Z");
    }
}
