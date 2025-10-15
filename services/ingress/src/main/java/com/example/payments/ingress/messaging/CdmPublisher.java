package com.example.payments.ingress.messaging;

import com.example.payments.cdm.Envelope;
import com.example.payments.cdm.PaymentInstruction;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CdmPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CdmPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String topic, Envelope envelope, PaymentInstruction instruction) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, envelope.messageId(), instruction);
        record.headers().add("messageName", envelope.messageName().getBytes());
        record.headers().add("messageVersion", envelope.messageVersion().getBytes());
        record.headers().add("schemaUri", envelope.schemaUri().getBytes());
        record.headers().add("correlationId", envelope.correlationId().getBytes());
        record.headers().add("endToEndId", envelope.endToEndId().getBytes());
        record.headers().add("sha256", envelope.sha256().getBytes());
        kafkaTemplate.send(record);
    }
}
