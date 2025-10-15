package com.example.payments.status.api;

import com.example.payments.status.persistence.PaymentStatusDocument;
import com.example.payments.status.persistence.PaymentStatusRepository;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatusControllerTest {

    private final PaymentStatusRepository repository = mock(PaymentStatusRepository.class);
    private final StatusController controller = new StatusController(repository);

    @Test
    void shouldReturnStatusesForInstruction() {
        when(repository.findByInstructionId("ABC123"))
                .thenReturn(Flux.just(new PaymentStatusDocument("ABC123", "RECEIVED", Instant.now(), "NONE")));

        StepVerifier.create(controller.byInstruction("ABC123"))
                .expectNextMatches(status -> "ABC123".equals(status.getInstructionId()))
                .verifyComplete();
    }
}
