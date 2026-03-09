package com.nurdor_project.volunteer_service.proxy;

import com.nurdor_project.volunteer_service.dto.EventsLogDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "events-log-service")
public interface EventsLogProxy {

    @GetMapping("/api/admin/eventLogs/findVolunteerIds/{idEvent}")
    List<Integer> findVolunteerIds(@PathVariable Integer idEvent);

    @GetMapping("/api/admin/eventLogs/findPresentVolunteerIds")
    List<EventsLogDto> findEventsLogsByActiveEventsIds();
}
