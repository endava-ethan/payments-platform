package com.example.payments.status.api;

import com.example.payments.status.persistence.PaymentStatusDocument;
import com.example.payments.status.persistence.PaymentStatusRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(path = "/api/v1/status", produces = MediaType.APPLICATION_JSON_VALUE)
public class StatusController {

    private final PaymentStatusRepository repository;

    public StatusController(PaymentStatusRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{instructionId}")
    public Flux<PaymentStatusDocument> byInstruction(@PathVariable String instructionId) {
        return repository.findByInstructionId(instructionId);
    }
}
