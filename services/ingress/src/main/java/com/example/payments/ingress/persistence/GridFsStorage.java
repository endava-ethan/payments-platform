package com.example.payments.ingress.persistence;

import org.bson.Document;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Component
public class GridFsStorage {

    private final GridFsTemplate gridFsTemplate;

    public GridFsStorage(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    public String storeOriginalXml(String filename, String xml) {
        Document metadata = new Document("contentType", "application/xml");
        return gridFsTemplate.store(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), filename, metadata).toString();
    }
}
