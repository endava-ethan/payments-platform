package com.example.payments.egress.messaging;

import com.example.payments.cdm.PaymentStatus;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StatusListener {

    private static final Logger log = LoggerFactory.getLogger(StatusListener.class);
    private final Pacs002MessageBuilder builder;

    public StatusListener(Pacs002MessageBuilder builder) {
        this.builder = builder;
    }

    @KafkaListener(topics = "${payments.topics.status}", groupId = "egress")
    public void onStatus(ConsumerRecord<String, PaymentStatus> record) {
        PaymentStatus status = record.value();
        String xml = builder.toXml(status);
        log.info("Dispatching status message {} with correlation headers {}", status.instructionId(), record.headers());
        // A connector would publish xml to external network (MQ, REST, etc.).
        // We intentionally avoid logging raw XML to satisfy security controls.
    }
}
