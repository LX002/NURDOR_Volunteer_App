package com.nurdor_project.events_log_service.controller;

import com.nurdor_project.events_log_service.dto.EventsLogDto;
import com.nurdor_project.events_log_service.model.EventsLog;
import com.nurdor_project.events_log_service.service.EventsLogService;
import com.nurdor_project.events_log_service.utils.EventsLogMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class EventsLogController {

    private EventsLogService eventsLogService;

    @PostMapping("/volunteer/eventsLogs/insert")
    public ResponseEntity<EventsLog> insert(@RequestBody EventsLogDto eventsLogDto) {
        EventsLog saved = eventsLogService.insertLog(EventsLogMapper.mapToEntity(eventsLogDto));
        return saved != null
                ? ResponseEntity.ok(saved)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
