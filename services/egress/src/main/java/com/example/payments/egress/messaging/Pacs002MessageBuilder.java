package com.example.payments.egress.messaging;

import com.example.payments.cdm.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class Pacs002MessageBuilder {

    public String toXml(PaymentStatus status) {
        return """
                <Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pacs.002.001.10\">
                  <FIToFIPmtStsRpt>
                    <GrpHdr>
                      <MsgId>%s</MsgId>
                      <CreDtTm>%s</CreDtTm>
                    </GrpHdr>
                    <OrgnlGrpInfAndSts>
                      <Sts>%s</Sts>
                      <StsRsnInf>
                        <Rsn>%s</Rsn>
                      </StsRsnInf>
                    </OrgnlGrpInfAndSts>
                  </FIToFIPmtStsRpt>
                </Document>
                """.formatted(status.instructionId(), status.updatedAt(), status.status(), status.reasonCode());
    }
}
