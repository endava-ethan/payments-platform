package com.example.payments.ingress.api;

import com.example.payments.ingress.service.IsoInboundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "ISO 20022 Ingress")
public class IsoInboundController {

    private static final String SAMPLE_PACS008 = """
            <?xml version="1.0" encoding="UTF-8"?>
            <FIToFICstmrCdtTrf xmlns="urn:iso:std:iso:20022:tech:xsd:pacs.008.001.10">
              <GrpHdr>
                <MsgId>ABC123</MsgId>
                <CreDtTm>2024-05-01T10:15:30Z</CreDtTm>
              </GrpHdr>
              <CdtTrfTxInf>
                <PmtId>
                  <InstrId>ABC123</InstrId>
                  <EndToEndId>E2E-789</EndToEndId>
                </PmtId>
                <IntrBkSttlmAmt>199.99</IntrBkSttlmAmt>
                <DbtrAcct>
                  <IBAN>DE89370400440532013000</IBAN>
                </DbtrAcct>
                <CdtrAcct>
                  <IBAN>FR7630006000011234567890189</IBAN>
                </CdtrAcct>
              </CdtTrfTxInf>
            </FIToFICstmrCdtTrf>
            """;

    private final IsoInboundService inboundService;

    public IsoInboundController(IsoInboundService inboundService) {
        this.inboundService = inboundService;
    }

    @Operation(
            summary = "Ingest an ISO 20022 pacs.008 message",
            description = "Validates the provided pacs.008 XML, persists the original payload, and publishes a canonical payment instruction.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_XML_VALUE,
                            schema = @Schema(type = "string"),
                            examples = @ExampleObject(name = "pacs.008", value = SAMPLE_PACS008)
                    )
            ),
            parameters = {
                    @Parameter(name = "X-Correlation-Id", required = true, description = "Correlation identifier supplied by the upstream channel"),
                    @Parameter(name = "X-End-To-End-Id", required = true, description = "End-to-end identifier for downstream clearing references"),
                    @Parameter(name = "X-Message-Name", required = false, description = "ISO 20022 message name (defaults to pacs.008)"),
                    @Parameter(name = "X-Message-Version", required = false, description = "ISO 20022 message version (defaults to 10)")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "202",
                            description = "Message accepted for processing",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "object", example = "{\"messageId\":\"ABC123\",\"correlationId\":\"corr-123\",\"sha256\":\"ff6a1df...\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Schema or semantic validation failed", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "409", description = "Duplicate message detected", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
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
