package com.nurdorproject.event_service.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "events-log-service")
public interface EventsLogProxy {

    @PostMapping("/api/admin/eventsLogs/dismissVolunteers/{idEvent}")
    String dismissVolunteers(@PathVariable Integer idEvent);

    @DeleteMapping("/api/volunteer/eventsLogs/deleteLogs")
    String deleteLogsByIdEvent(@RequestParam("idEvent") Integer idEvent);
}
