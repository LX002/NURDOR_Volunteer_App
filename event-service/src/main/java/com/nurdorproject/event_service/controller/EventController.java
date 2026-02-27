package com.nurdorproject.event_service.controller;

import com.nurdorproject.event_service.dto.EventDto;
import com.nurdorproject.event_service.model.Event;
import com.nurdorproject.event_service.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class EventController {

    private EventService eventService;

    @GetMapping("/volunteer/events/getEvents")
    public ResponseEntity<List<EventDto>> findAll() {
        List<EventDto> events = eventService.findAll().stream()
                .map(e -> {
                    byte[] pic = e.getEventImg();
                    return new EventDto(
                            e.getId(), e.getEventName(), e.getDescription(),
                            e.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            e.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            e.getLatitude(), e.getLongitude(), pic != null ? Base64.getEncoder().encodeToString(e.getEventImg()) : null,
                            e.getLocationDesc(), e.getCity());
                }).toList();
        return !events.isEmpty()
                ? ResponseEntity.ok(events)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
