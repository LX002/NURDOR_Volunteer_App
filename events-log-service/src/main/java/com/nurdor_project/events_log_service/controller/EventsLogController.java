package com.nurdor_project.events_log_service.controller;

import com.nurdor_project.events_log_service.dto.EventsLogDto;
import com.nurdor_project.events_log_service.model.EventsLog;
import com.nurdor_project.events_log_service.service.EventsLogService;
import com.nurdor_project.events_log_service.utils.EventsLogMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class EventsLogController {

    private EventsLogService eventsLogService;

    // TODO: rename to pick event? or proxy up with event
    @PostMapping("/volunteer/eventsLogs/insert")
    public ResponseEntity<EventsLog> insert(@RequestBody @Valid EventsLogDto eventsLogDto) {
        EventsLog saved = eventsLogService.insertLog(EventsLogMapper.mapToEntity(eventsLogDto));
        return saved != null
                ? ResponseEntity.ok(saved)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/volunteer/eventLogs/updatePresence")
    public ResponseEntity<EventsLog> markAsPresent(@RequestBody @Valid EventsLogDto eventsLogDto) {
        return ResponseEntity.ok(eventsLogService.updatePresence(eventsLogDto));
    }

    @GetMapping("/admin/eventLogs/findVolunteerIds/{idEvent}")
    public ResponseEntity<List<Integer>> findVolunteerIds(@PathVariable @Min(1) Integer idEvent) {
        return ResponseEntity.ok(eventsLogService.findVolunteerIdsByIdEvent(idEvent));
    }

    @GetMapping("/admin/eventLogs/findPresentVolunteerIds")
    public ResponseEntity<List<EventsLog>> findEventsLogsByActiveEventsIds() {
        return ResponseEntity.ok(eventsLogService.findEventsLogsByActiveEventsIds());
    }

    @PostMapping("/admin/eventsLogs/dismissVolunteers/{idEvent}")
    public ResponseEntity<String> dismissVolunteers(@PathVariable @Min(1) Integer idEvent) {
        String[] results = eventsLogService.dismissVolunteers(idEvent).split(":");
        return results[0].equals("204")
                ? new ResponseEntity<>(results[1], HttpStatus.NO_CONTENT)
                : ResponseEntity.ok(results[1]);
    }
}
