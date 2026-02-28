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
                // auth-service routes
                .route("login", r -> r.path("/login")
                        .filters(f -> f.rewritePath("/login", "/api/auth/login"))
                        .uri("lb://AUTH-SERVICE"))
                .route("register", r -> r.path("/register")
                        .filters(f -> f.rewritePath("/register", "/api/auth/register"))
                        .uri("lb://AUTH-SERVICE"))
                // volunteer-service routes
                .route("admin-volunteers", r -> r.path("/admin/volunteers")
                        .filters(f -> f.rewritePath("/admin/volunteers", "/api/admin/volunteers/findAll"))
                        .uri("lb://VOLUNTEER-SERVICE"))
                // event-service routes
                .route("get-events", r -> r.path("/volunteer/getEvents")
                        .filters(f -> f.rewritePath("/volunteer/getEvents", "/api/volunteer/events/getEvents"))
                        .uri("lb://EVENT-SERVICE"))
                // events-log-service routes
                .route("insert-log", r -> r.path("/volunteer/insertLog")
                        .filters(f -> f.rewritePath("/volunteer/insertLog", "/api/volunteer/eventsLogs/insert"))
                        .uri("lb://EVENTS-LOG-SERVICE"))
                .build();
    }
}
