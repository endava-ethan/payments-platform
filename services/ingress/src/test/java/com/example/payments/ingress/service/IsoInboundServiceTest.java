package com.example.payments.ingress.service;

import com.example.payments.cdm.PaymentInstruction;
import com.example.payments.ingress.messaging.CdmPublisher;
import com.example.payments.ingress.persistence.GridFsStorage;
import com.example.payments.ingress.persistence.InboundMessageDocument;
import com.example.payments.ingress.persistence.InboundMessageRepository;
import com.example.payments.ingress.xml.IsoPacs008Extractor;
import com.example.payments.ingress.xml.IsoXmlParser;
import com.example.payments.validation.ValidationContext;
import com.example.payments.validation.ValidationPipeline;
import com.example.payments.validation.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IsoInboundServiceTest {

    private IsoXmlParser parser;
    private IsoPacs008Extractor extractor;
    private StubIsoPaymentMapper mapper;
    private StubValidationPipeline<Document> schemaPipeline;
    private StubValidationPipeline<PaymentInstruction> semanticPipeline;
    private GridFsStorage storage;
    private StubInboundMessageRepository repository;
    private StubCdmPublisher publisher;

    private IsoInboundService service;

    @BeforeEach
    void setUp() throws Exception {
        parser = new IsoXmlParser();
        extractor = new IsoPacs008Extractor();
        schemaPipeline = new StubValidationPipeline<>();
        semanticPipeline = new StubValidationPipeline<>();
        mapper = new StubIsoPaymentMapper();
        storage = new StubGridFsStorage();
        repository = new StubInboundMessageRepository();
        publisher = new StubCdmPublisher();
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
        schemaPipeline.setResult(ValidationResult.ok());
        mapper.setInstruction(new PaymentInstruction("ABC123", "DE89370400440532013000", "FR7630006000011234567890189", "EUR", new BigDecimal("199.99"), LocalDate.parse("2024-05-01"), null));
        semanticPipeline.setResult(ValidationResult.ok());
        repository.setNextResult(Optional.of(new InboundMessageDocument("pacs.008", "10", "ABC123", "corr", "e2e", "abc", Instant.now(), "1")));

        assertThatThrownBy(() -> service.processInbound(xml, "pacs.008", "10", "corr", "e2e"))
                .isInstanceOf(IllegalStateException.class);
    }

    private static class StubValidationPipeline<T> extends ValidationPipeline<T> {
        private ValidationResult nextResult = ValidationResult.ok();

        void setResult(ValidationResult result) {
            this.nextResult = result;
        }

        @Override
        public ValidationResult validate(T target, ValidationContext context) {
            return nextResult;
        }
    }

    private static class StubGridFsStorage extends GridFsStorage {
        StubGridFsStorage() {
            super(null);
        }

        @Override
        public String storeOriginalXml(String filename, String xml) {
            return "gridfs-id";
        }
    }

    private static class StubIsoPaymentMapper implements com.example.payments.cdm.IsoPaymentMapper {
        private PaymentInstruction nextInstruction;

        void setInstruction(PaymentInstruction instruction) {
            this.nextInstruction = instruction;
        }

        @Override
        public PaymentInstruction toInstruction(com.example.payments.cdm.IsoPacs008Payment pacs008Payment) {
            if (nextInstruction == null) {
                throw new IllegalStateException("No instruction configured");
            }
            return nextInstruction;
        }
    }

    private static class StubInboundMessageRepository extends InboundMessageRepository {
        private Optional<InboundMessageDocument> nextResult = Optional.empty();

        StubInboundMessageRepository() {
            super(null);
        }

        void setNextResult(Optional<InboundMessageDocument> result) {
            this.nextResult = result;
        }

        @Override
        public Optional<InboundMessageDocument> findByEnvelope(com.example.payments.cdm.Envelope envelope) {
            return nextResult;
        }

        @Override
        public InboundMessageDocument save(com.example.payments.cdm.Envelope envelope, String gridFsId) {
            return nextResult.orElse(null);
        }
    }

    private static class StubCdmPublisher extends CdmPublisher {
        private boolean called;

        StubCdmPublisher() {
            super(null);
        }

        @Override
        public void publish(String topic, com.example.payments.cdm.Envelope envelope, PaymentInstruction instruction) {
            called = true;
        }

        boolean wasCalled() {
            return called;
        }
    }
}
