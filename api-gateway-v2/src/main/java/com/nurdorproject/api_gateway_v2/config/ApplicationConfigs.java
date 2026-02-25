package com.nurdorproject.api_gateway_v2.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfigs {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
