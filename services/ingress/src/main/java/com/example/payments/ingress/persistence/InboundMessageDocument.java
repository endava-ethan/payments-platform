package com.example.payments.ingress.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "inbound_messages")
@CompoundIndexes({
        @CompoundIndex(name = "unique_message",
                def = "{ 'messageName': 1, 'messageVersion': 1, 'messageId': 1, 'correlationId': 1 }",
                unique = true)
})
public class InboundMessageDocument {

    @Id
    private String id;
    private String messageName;
    private String messageVersion;
    private String messageId;
    private String correlationId;
    private String endToEndId;
    private String sha256;
    private Instant receivedAt;
    private String gridFsObjectId;

    public InboundMessageDocument(String messageName, String messageVersion, String messageId,
                                  String correlationId, String endToEndId, String sha256,
                                  Instant receivedAt, String gridFsObjectId) {
        this.messageName = messageName;
        this.messageVersion = messageVersion;
        this.messageId = messageId;
        this.correlationId = correlationId;
        this.endToEndId = endToEndId;
        this.sha256 = sha256;
        this.receivedAt = receivedAt;
        this.gridFsObjectId = gridFsObjectId;
    }

    public String getMessageName() {
        return messageName;
    }

    public String getMessageVersion() {
        return messageVersion;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getEndToEndId() {
        return endToEndId;
    }

    public String getSha256() {
        return sha256;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public String getGridFsObjectId() {
        return gridFsObjectId;
    }
}
