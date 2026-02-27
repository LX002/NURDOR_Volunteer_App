package com.nurdor_project.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("login", r -> r.path("/login")
                        .filters(f -> f.rewritePath("/login", "/api/auth/login"))
                        .uri("lb://AUTH-SERVICE"))
                .route("register", r -> r.path("/register")
                        .filters(f -> f.rewritePath("/register", "/api/auth/register"))
                        .uri("lb://AUTH-SERVICE"))
                .route("admin-volunteers", r -> r.path("/admin/volunteers")
                        .filters(f -> f.rewritePath("/admin/volunteers", "/api/admin/volunteers/findAll"))
                        .uri("lb://VOLUNTEER-SERVICE"))
                .build();
    }
}
