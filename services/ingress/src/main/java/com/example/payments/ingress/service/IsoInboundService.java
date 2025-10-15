package com.example.payments.ingress.service;

import com.example.payments.cdm.Envelope;
import com.example.payments.cdm.IsoPacs008Payment;
import com.example.payments.cdm.IsoPaymentMapper;
import com.example.payments.cdm.PaymentInstruction;
import com.example.payments.ingress.messaging.CdmPublisher;
import com.example.payments.ingress.persistence.GridFsStorage;
import com.example.payments.ingress.persistence.InboundMessageRepository;
import com.example.payments.ingress.xml.IsoPacs008Extractor;
import com.example.payments.ingress.xml.IsoXmlParser;
import com.example.payments.validation.ValidationContext;
import com.example.payments.validation.ValidationPipeline;
import com.example.payments.validation.ValidationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.time.Instant;
import java.util.Map;

@Service
public class IsoInboundService {

    private final IsoXmlParser isoXmlParser;
    private final IsoPacs008Extractor extractor;
    private final IsoPaymentMapper mapper;
    private final ValidationPipeline<Document> schemaValidationPipeline;
    private final ValidationPipeline<PaymentInstruction> semanticValidationPipeline;
    private final GridFsStorage gridFsStorage;
    private final InboundMessageRepository inboundMessageRepository;
    private final CdmPublisher publisher;
    private final String validatedTopic;

    public IsoInboundService(IsoXmlParser isoXmlParser,
                             IsoPacs008Extractor extractor,
                             IsoPaymentMapper mapper,
                             ValidationPipeline<Document> schemaValidationPipeline,
                             ValidationPipeline<PaymentInstruction> semanticValidationPipeline,
                             GridFsStorage gridFsStorage,
                             InboundMessageRepository inboundMessageRepository,
                             CdmPublisher publisher,
                             @Value("${payments.topics.validated}") String validatedTopic) {
        this.isoXmlParser = isoXmlParser;
        this.extractor = extractor;
        this.mapper = mapper;
        this.schemaValidationPipeline = schemaValidationPipeline;
        this.semanticValidationPipeline = semanticValidationPipeline;
        this.gridFsStorage = gridFsStorage;
        this.inboundMessageRepository = inboundMessageRepository;
        this.publisher = publisher;
        this.validatedTopic = validatedTopic;
    }

    public Map<String, Object> processInbound(String isoXml,
                                              String messageName,
                                              String messageVersion,
                                              String correlationId,
                                              String endToEndId) {
        Document document = isoXmlParser.parse(isoXml);
        ValidationResult schemaResult = schemaValidationPipeline.validate(document, ValidationContext.empty());
        if (!schemaResult.valid()) {
            throw new IllegalArgumentException("Schema validation failed: " + schemaResult.errors());
        }

        IsoPacs008Payment isoPayment = extractor.extract(document);
        PaymentInstruction instruction = mapper.toInstruction(isoPayment);
        ValidationResult semanticResult = semanticValidationPipeline.validate(instruction, ValidationContext.empty());
        if (!semanticResult.valid()) {
            throw new IllegalArgumentException("Semantic validation failed: " + semanticResult.errors());
        }

        String sha256 = isoXmlParser.sha256Hex(isoXml);
        Envelope envelope = new Envelope(
                messageName,
                messageVersion,
                "urn:iso:std:iso:20022:tech:xsd:" + messageName + ".001." + messageVersion,
                correlationId,
                endToEndId,
                instruction.instructionId(),
                sha256,
                Instant.now()
        );

        inboundMessageRepository.findByEnvelope(envelope).ifPresent(existing -> {
            throw new IllegalStateException("Duplicate message for correlationId " + correlationId);
        });

        String gridFsId = gridFsStorage.storeOriginalXml(envelope.messageId() + ".xml", isoXml);
        inboundMessageRepository.save(envelope, gridFsId);

        publisher.publish(validatedTopic, envelope, instruction);

        return Map.of(
                "messageId", envelope.messageId(),
                "correlationId", envelope.correlationId(),
                "sha256", envelope.sha256()
        );
    }
}
