package com.example.payments.orchestrator.messaging;

import com.example.payments.cdm.PaymentInstruction;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaOperations;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentOrchestratorTest {

    @Test
    void shouldForwardHeadersAndEmitStatus() {
        RecordingKafkaOperations kafkaTemplate = new RecordingKafkaOperations();
        PaymentOrchestrator orchestrator = new PaymentOrchestrator(kafkaTemplate, "iso20022.outbound.pacs.002.v9");
        PaymentInstruction instruction = new PaymentInstruction("ABC123", "DE89370400440532013000", "FR7630006000011234567890189", "EUR", java.math.BigDecimal.ONE, java.time.LocalDate.now(), null);
        ConsumerRecord<String, PaymentInstruction> record = new ConsumerRecord<>("cdm.validated.payment.v1", 0, 0L, "ABC123", instruction);
        record.headers().add(new RecordHeader("correlationId", "corr".getBytes(StandardCharsets.UTF_8)));

        orchestrator.onValidated(record);

        assertThat(kafkaTemplate.lastRecord).isNotNull();
        assertThat(kafkaTemplate.lastRecord.topic()).isEqualTo("iso20022.outbound.pacs.002.v9");
        assertThat(kafkaTemplate.lastRecord.headers().lastHeader("correlationId").value()).isEqualTo("corr".getBytes(StandardCharsets.UTF_8));
    }

    private static class RecordingKafkaOperations implements KafkaOperations<String, Object> {
        private org.apache.kafka.clients.producer.ProducerRecord<String, Object> lastRecord;

        @Override
        public CompletableFuture<org.springframework.kafka.support.SendResult<String, Object>> sendDefault(Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CompletableFuture<org.springframework.kafka.support.SendResult<String, Object>> sendDefault(String key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CompletableFuture<org.springframework.kafka.support.SendResult<String, Object>> sendDefault(Integer partition, String key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CompletableFuture<org.springframework.kafka.support.SendResult<String, Object>> sendDefault(Integer partition, Long timestamp, String key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CompletableFuture<org.springframework.kafka.support.SendResult<String, Object>> send(String topic, Object data) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CompletableFuture<org.springframework.kafka.support.SendResult<String, Object>> send(String topic, String key, Object data) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CompletableFuture<org.springframework.kafka.support.SendResult<String, Object>> send(String topic, Integer partition, String key, Object data) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CompletableFuture<org.springframework.kafka.support.SendResult<String, Object>> send(String topic, Integer partition, Long timestamp, String key, Object data) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CompletableFuture<org.springframework.kafka.support.SendResult<String, Object>> send(org.apache.kafka.clients.producer.ProducerRecord<String, Object> record) {
            this.lastRecord = record;
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletableFuture<org.springframework.kafka.support.SendResult<String, Object>> send(org.springframework.messaging.Message<?> message) {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.util.List<org.apache.kafka.common.PartitionInfo> partitionsFor(String topic) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<org.apache.kafka.common.MetricName, ? extends org.apache.kafka.common.Metric> metrics() {
            return Map.of();
        }

        @Override
        public <T> T execute(ProducerCallback<String, Object, T> callback) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T executeInTransaction(OperationsCallback<String, Object, T> callback) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void flush() {
            // no-op
        }

        @Override
        public void sendOffsetsToTransaction(Map<org.apache.kafka.common.TopicPartition, org.apache.kafka.clients.consumer.OffsetAndMetadata> offsets, org.apache.kafka.clients.consumer.ConsumerGroupMetadata consumerGroupMetadata) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isTransactional() {
            return false;
        }

        @Override
        public boolean isAllowNonTransactional() {
            return true;
        }

        @Override
        public boolean inTransaction() {
            return false;
        }

        @Override
        public org.springframework.kafka.core.ProducerFactory<String, Object> getProducerFactory() {
            return null;
        }

        @Override
        public org.apache.kafka.clients.consumer.ConsumerRecord<String, Object> receive(String topic, int partition, long offset) {
            throw new UnsupportedOperationException();
        }

        @Override
        public org.apache.kafka.clients.consumer.ConsumerRecord<String, Object> receive(String topic, int partition, long offset, java.time.Duration pollTimeout) {
            throw new UnsupportedOperationException();
        }

        @Override
        public org.apache.kafka.clients.consumer.ConsumerRecords<String, Object> receive(java.util.Collection<org.springframework.kafka.support.TopicPartitionOffset> requested) {
            throw new UnsupportedOperationException();
        }

        @Override
        public org.apache.kafka.clients.consumer.ConsumerRecords<String, Object> receive(java.util.Collection<org.springframework.kafka.support.TopicPartitionOffset> requested, java.time.Duration pollTimeout) {
            throw new UnsupportedOperationException();
        }
    }
}
