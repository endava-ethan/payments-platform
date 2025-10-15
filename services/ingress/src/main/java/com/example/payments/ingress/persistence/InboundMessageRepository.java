package com.example.payments.ingress.persistence;

import com.example.payments.cdm.Envelope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public class InboundMessageRepository {

    private final MongoTemplate mongoTemplate;

    public InboundMessageRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Optional<InboundMessageDocument> findByEnvelope(Envelope envelope) {
        Query query = Query.query(Criteria.where("messageName").is(envelope.messageName())
                .and("messageVersion").is(envelope.messageVersion())
                .and("messageId").is(envelope.messageId())
                .and("correlationId").is(envelope.correlationId()));
        return Optional.ofNullable(mongoTemplate.findOne(query, InboundMessageDocument.class));
    }

    public InboundMessageDocument save(Envelope envelope, String gridFsId) {
        InboundMessageDocument document = new InboundMessageDocument(
                envelope.messageName(),
                envelope.messageVersion(),
                envelope.messageId(),
                envelope.correlationId(),
                envelope.endToEndId(),
                envelope.sha256(),
                Optional.ofNullable(envelope.receivedAt()).orElseGet(Instant::now),
                gridFsId
        );
        return mongoTemplate.save(document);
    }
}
