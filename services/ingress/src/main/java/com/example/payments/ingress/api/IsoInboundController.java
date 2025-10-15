package com.example.payments.ingress.api;

import com.example.payments.ingress.service.IsoInboundService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/payments/iso20022", consumes = MediaType.APPLICATION_XML_VALUE)
@Validated
public class IsoInboundController {

    private final IsoInboundService inboundService;

    public IsoInboundController(IsoInboundService inboundService) {
        this.inboundService = inboundService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, Object> ingest(@RequestBody String isoXml,
                                      @RequestHeader("X-Correlation-Id") String correlationId,
                                      @RequestHeader("X-End-To-End-Id") String endToEndId,
                                      @RequestHeader(value = "X-Message-Name", defaultValue = "pacs.008") String messageName,
                                      @RequestHeader(value = "X-Message-Version", defaultValue = "10") String messageVersion) {
        return inboundService.processInbound(isoXml, messageName, messageVersion, correlationId, endToEndId);
    }
}
