package com.example.payments.status.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface PaymentStatusRepository extends ReactiveMongoRepository<PaymentStatusDocument, String> {
    Flux<PaymentStatusDocument> findByInstructionId(String instructionId);
}
