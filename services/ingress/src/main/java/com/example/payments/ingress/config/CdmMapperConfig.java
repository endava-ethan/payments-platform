package com.example.payments.ingress.config;

import com.example.payments.cdm.DefaultIsoPaymentMapper;
import com.example.payments.cdm.IsoPaymentMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CdmMapperConfig {

    @Bean
    IsoPaymentMapper isoPaymentMapper() {
        return new DefaultIsoPaymentMapper();
    }
}
