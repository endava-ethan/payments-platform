package com.example.payments.cdm;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface IsoPaymentMapper {

    @Mapping(target = "instructionId", source = "msgId")
    PaymentInstruction toInstruction(IsoPacs008Payment pacs008Payment);
}
