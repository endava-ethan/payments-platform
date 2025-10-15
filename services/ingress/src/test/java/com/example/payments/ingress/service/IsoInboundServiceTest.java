package com.example.payments.ingress.service;

import com.example.payments.cdm.PaymentInstruction;
import com.example.payments.ingress.messaging.CdmPublisher;
import com.example.payments.ingress.persistence.GridFsStorage;
import com.example.payments.ingress.persistence.InboundMessageRepository;
import com.example.payments.ingress.xml.IsoPacs008Extractor;
import com.example.payments.ingress.xml.IsoXmlParser;
import com.example.payments.validation.ValidationContext;
import com.example.payments.validation.ValidationPipeline;
import com.example.payments.validation.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.Document;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class IsoInboundServiceTest {

    private IsoXmlParser parser;
    private IsoPacs008Extractor extractor;
    @Mock
    private com.example.payments.cdm.IsoPaymentMapper mapper;
    @Mock
    private ValidationPipeline<Document> schemaPipeline;
    @Mock
    private ValidationPipeline<PaymentInstruction> semanticPipeline;
    @Mock
    private GridFsStorage storage;
    @Mock
    private InboundMessageRepository repository;
    @Mock
    private CdmPublisher publisher;

    private IsoInboundService service;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        parser = new IsoXmlParser();
        extractor = new IsoPacs008Extractor();
        service = new IsoInboundService(parser, extractor, mapper, schemaPipeline, semanticPipeline, storage, repository, publisher, "cdm.validated.payment.v1");
    }

    @Test
    void shouldRejectDuplicateMessages() {
        String xml = """
                <?xml version=\"1.0\" encoding=\"UTF-8\"?>
                <FIToFICstmrCdtTrf xmlns=\"urn:iso:std:iso:20022:tech:xsd:pacs.008.001.10\">
                    <GrpHdr>
                        <MsgId>ABC123</MsgId>
                        <CreDtTm>2024-05-01T10:15:30Z</CreDtTm>
                    </GrpHdr>
                    <CdtTrfTxInf>
                        <PmtId>
                            <InstrId>ABC123</InstrId>
                            <EndToEndId>E2E-789</EndToEndId>
                        </PmtId>
                        <IntrBkSttlmAmt Ccy=\"EUR\">199.99</IntrBkSttlmAmt>
                        <DbtrAcct>
                            <IBAN>DE89370400440532013000</IBAN>
                        </DbtrAcct>
                        <CdtrAcct>
                            <IBAN>FR7630006000011234567890189</IBAN>
                        </CdtrAcct>
                    </CdtTrfTxInf>
                </FIToFICstmrCdtTrf>
                """;
        when(schemaPipeline.validate(any(Document.class), any(ValidationContext.class))).thenReturn(ValidationResult.ok());
        when(mapper.toInstruction(any())).thenReturn(new PaymentInstruction("ABC123", "DE89370400440532013000", "FR7630006000011234567890189", "EUR", new java.math.BigDecimal("199.99"), java.time.LocalDate.parse("2024-05-01"), null));
        when(semanticPipeline.validate(any(), any())).thenReturn(ValidationResult.ok());
        when(repository.findByEnvelope(any())).thenReturn(Optional.of(new com.example.payments.ingress.persistence.InboundMessageDocument("pacs.008", "10", "ABC123", "corr", "e2e", "abc", Instant.now(), "1")));

        assertThatThrownBy(() -> service.processInbound(xml, "pacs.008", "10", "corr", "e2e"))
                .isInstanceOf(IllegalStateException.class);
    }
}
