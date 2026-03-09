package com.nurdor_project.events_log_service.proxy;

import com.nurdor_project.events_log_service.dto.EventDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "event-service")
public interface EventProxy {

    @GetMapping("/api/volunteer/events/findById/{idEvent}")
    EventDto findEventById(@PathVariable Integer idEvent);

    @GetMapping("/api/admin/events/started")
    List<EventDto> findStartedEvents();
}
