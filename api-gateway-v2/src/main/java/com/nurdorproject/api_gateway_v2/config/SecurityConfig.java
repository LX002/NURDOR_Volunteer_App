package com.nurdorproject.api_gateway_v2.config;

import com.nurdorproject.api_gateway_v2.security.JwtFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private JwtFilter jwtFilter;
}
