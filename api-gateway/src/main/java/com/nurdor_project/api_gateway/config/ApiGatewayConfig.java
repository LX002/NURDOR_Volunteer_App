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
                .route("admin-volunteers", r -> r.path("/volunteer/volunteers")
                        .filters(f -> f.rewritePath("/volunteer/volunteers", "/api/volunteer/volunteers/findAll"))
                        .uri("lb://VOLUNTEER-SERVICE"))

                // event-service routes
                .route("get-event", r -> r.path("/volunteer/event/{idEvent}")
                        .filters(f -> f.rewritePath("/volunteer/event/(?<idEvent>.*)", "/api/volunteer/events/findById/${idEvent}"))
                        .uri("lb://EVENT-SERVICE"))
                .route("get-events", r -> r.path("/volunteer/getEvents")
                        .filters(f -> f.rewritePath("/volunteer/getEvents", "/api/volunteer/events/getEvents"))
                        .uri("lb://EVENT-SERVICE"))
                .route("download-pdf", r -> r.path("/volunteer/eventPdf/{idEvent}")
                        .filters(f -> f.rewritePath("/volunteer/eventPdf/(?<idEvent>.*)", "/api/volunteer/events/getPdfById/${idEvent}")
                                .setResponseHeader("Content-Disposition", "attachment"))
                        .uri("lb://EVENT-SERVICE"))
                .route("start-event", r -> r.path("/admin/start")
                        .filters(f -> f.rewritePath("/admin/start", "/api/admin/events/start"))
                        .uri("lb://EVENT-SERVICE"))
                .route("end-event", r -> r.path("/admin/end/{idEvent}")
                        .filters(f -> f.rewritePath("/admin/end/(?<idEvent>.*)", "/api/admin/events/end/${idEvent}"))
                        .uri("lb://EVENT-SERVICE"))

                // events-log-service routes
                .route("insert-log", r -> r.path("/volunteer/insertLog")
                        .filters(f -> f.rewritePath("/volunteer/insertLog", "/api/volunteer/eventsLogs/insert"))
                        .uri("lb://EVENTS-LOG-SERVICE"))
                .route("update-presence", r -> r.path("/volunteer/updatePresence")
                        .filters(f -> f.rewritePath("/volunteer/updatePresence", "/api/volunteer/eventLogs/updatePresence"))
                        .uri("lb://EVENTS-LOG-SERVICE"))

                // donations-service routes
                .route("donate", r -> r.path("/volunteer/addDonation")
                        .filters(f -> f.rewritePath("/volunteer/addDonation", "/api/volunteer/stands/addDonation"))
                        .uri("lb://DONATIONS-SERVICE"))

                // statistics-service routes
                .route("total-donations", r -> r.path("/admin/donations/{groupType}")
                        .filters(f -> f.rewritePath("/admin/donations/(?<groupType>.*)", "/api/admin/statistics/totalDonations/${groupType}"))
                        .uri("lb://STATISTICS-SERVICE"))
                .route("count-volunteers-by-cities", r -> r.path("/admin/countVolunteers")
                        .filters(f -> f.rewritePath("/admin/countVolunteers", "/api/admin/statistics/count/volunteersByCities"))
                        .uri("lb://STATISTICS-SERVICE"))
                .route("count-volunteers-on-event", r -> r.path("/admin/volunteersOnEvent")
                        .filters(f -> f.rewritePath("/admin/volunteersOnEvent", "/api/admin/statistics/count/volunteers"))
                        .uri("lb://STATISTICS-SERVICE"))
                .route("count-present-volunteers", r -> r.path("/admin/presentVolunteers")
                        .filters(f -> f.rewritePath("/admin/presentVolunteers", "/api/admin/statistics/count/presentVolunteers"))
                        .uri("lb://STATISTICS-SERVICE"))
                .route("count-started-events", r -> r.path("/admin/startedEvents")
                        .filters(f -> f.rewritePath("/admin/startedEvents", "/api/admin/statistics/count/startedEvents"))
                        .uri("lb://STATISTICS-SERVICE"))
                .build();
    }
}
