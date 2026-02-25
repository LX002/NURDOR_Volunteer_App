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
                        .uri("lb://VOLUNTEER-SERVICE")
                )
//                .route("login-failure", r -> r.path("/login")
//                        .and().query("error=true")
//                        .uri("lb://VOLUNTEER-SERVICE")
//                )
                .route("welcome", r -> r.path("/welcome")
                        .filters(f -> f.rewritePath("/welcome", "/api/welcome"))
                        .uri("lb://VOLUNTEER-SERVICE")
                )
                .route("hello", r -> r.path("/hello")
                        .filters(f -> f.rewritePath("/hello", "/api/hello"))
                        .uri("lb://VOLUNTEER-SERVICE")
                )
                .route("logout", r -> r.path("/logout")
                        .uri("lb://VOLUNTEER-SERVICE")
                )
                .build();
    }
}
