package com.example.payments.orchestrator.messaging;

import com.example.payments.cdm.PaymentInstruction;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PaymentOrchestratorTest {

    @Test
    void shouldForwardHeadersAndEmitStatus() {
        KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
        PaymentOrchestrator orchestrator = new PaymentOrchestrator(kafkaTemplate, "iso20022.outbound.pacs.002.v9");
        PaymentInstruction instruction = new PaymentInstruction("ABC123", "DE89370400440532013000", "FR7630006000011234567890189", "EUR", java.math.BigDecimal.ONE, java.time.LocalDate.now(), null);
        ConsumerRecord<String, PaymentInstruction> record = new ConsumerRecord<>("cdm.validated.payment.v1", 0, 0L, "ABC123", instruction);
        record.headers().add(new RecordHeader("correlationId", "corr".getBytes(StandardCharsets.UTF_8)));

        orchestrator.onValidated(record);

        ArgumentCaptor<org.apache.kafka.clients.producer.ProducerRecord<String, Object>> captor = ArgumentCaptor.forClass(org.apache.kafka.clients.producer.ProducerRecord.class);
        verify(kafkaTemplate).send(captor.capture());
        org.apache.kafka.clients.producer.ProducerRecord<String, Object> sent = captor.getValue();
        assertThat(sent.topic()).isEqualTo("iso20022.outbound.pacs.002.v9");
        assertThat(sent.headers().lastHeader("correlationId").value()).isEqualTo("corr".getBytes(StandardCharsets.UTF_8));
    }
}
