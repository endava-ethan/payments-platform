package com.example.payments.ingress.validation;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;

@Component
public class DocumentSchemaValidator {

    private final Schema schema;

    public DocumentSchemaValidator() throws IOException, SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        this.schema = factory.newSchema(new ClassPathResource("xsd/pacs.008.001.10.xsd").getURL());
    }

    public com.example.payments.validation.ValidationResult validate(Document document) {
        try {
            Validator validator = schema.newValidator();
            validator.validate(new DOMSource(document));
            return com.example.payments.validation.ValidationResult.ok();
        } catch (Exception e) {
            return com.example.payments.validation.ValidationResult.failed(java.util.List.of(e.getMessage()));
        }
    }
}
