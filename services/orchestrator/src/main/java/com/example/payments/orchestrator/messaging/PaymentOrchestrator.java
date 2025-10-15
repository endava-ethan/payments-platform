package com.example.payments.orchestrator.messaging;

import com.example.payments.cdm.Envelope;
import com.example.payments.cdm.PaymentInstruction;
import com.example.payments.cdm.PaymentStatus;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PaymentOrchestrator {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String statusTopic;

    public PaymentOrchestrator(KafkaTemplate<String, Object> kafkaTemplate,
                               @Value("${payments.topics.status}") String statusTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.statusTopic = statusTopic;
    }

    @KafkaListener(topics = "${payments.topics.validated}", groupId = "orchestrator")
    public void onValidated(ConsumerRecord<String, PaymentInstruction> record) {
        PaymentInstruction instruction = record.value();
        PaymentStatus status = new PaymentStatus(instruction.instructionId(), "RECEIVED", Instant.now(), null);
        ProducerRecord<String, Object> statusRecord = new ProducerRecord<>(statusTopic, instruction.instructionId(), status);
        record.headers().forEach(header -> statusRecord.headers().add(header));
        kafkaTemplate.send(statusRecord);
    }
}
