package com.example.payments.cdm;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class IsoPaymentMapperTest {

    private final IsoPaymentMapper mapper = Mappers.getMapper(IsoPaymentMapper.class);

    @Test
    void shouldMapIsoPacs008ToInstructionDeterministically() {
        IsoPacs008Payment iso = new IsoPacs008Payment(
                "ABC123",
                "DE89370400440532013000",
                "FR7630006000011234567890189",
                "EUR",
                new BigDecimal("199.99"),
                LocalDate.parse("2024-05-01"),
                "SALA"
        );

        PaymentInstruction instruction = mapper.toInstruction(iso);

        assertThat(instruction)
                .usingRecursiveComparison()
                .isEqualTo(new PaymentInstruction(
                        "ABC123",
                        "DE89370400440532013000",
                        "FR7630006000011234567890189",
                        "EUR",
                        new BigDecimal("199.99"),
                        LocalDate.parse("2024-05-01"),
                        "SALA"
                ));
    }
}
