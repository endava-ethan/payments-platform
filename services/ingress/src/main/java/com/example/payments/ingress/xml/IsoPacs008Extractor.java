package com.example.payments.ingress.xml;

import com.example.payments.cdm.IsoPacs008Payment;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
public class IsoPacs008Extractor {

    private static final String NS = "urn:iso:std:iso:20022:tech:xsd:pacs.008.001.10";

    public IsoPacs008Payment extract(Document document) {
        String msgId = textContent(document, "MsgId");
        String debtorIban = firstIban(document, 0);
        String creditorIban = firstIban(document, 1);
        Node amountNode = document.getElementsByTagNameNS(NS, "IntrBkSttlmAmt").item(0);
        String currency = amountNode.getAttributes().getNamedItem("Ccy").getNodeValue();
        BigDecimal amount = new BigDecimal(amountNode.getTextContent());
        String creDtTm = textContent(document, "CreDtTm");
        return new IsoPacs008Payment(msgId, debtorIban, creditorIban, currency, amount,
                OffsetDateTime.parse(creDtTm).toLocalDate(), null);
    }

    private String textContent(Document document, String tag) {
        return document.getElementsByTagNameNS(NS, tag).item(0).getTextContent();
    }

    private String firstIban(Document document, int index) {
        return document.getElementsByTagNameNS(NS, "IBAN").item(index).getTextContent();
    }
}
