package com.rzk.nurdor.controller;

import com.rzk.nurdor.dto.EventDto;
import com.rzk.nurdor.model.Event;
import com.rzk.nurdor.model.EventsLog;
import com.rzk.nurdor.model.EventsLogDto;
import com.rzk.nurdor.repository.EventsLogRepository;
import com.rzk.nurdor.service.EventService;
import com.rzk.nurdor.service.VolunteerService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final VolunteerService volunteerService;

    public EventController(EventService eventService, VolunteerService volunteerService) {
        this.eventService = eventService;
        this.volunteerService = volunteerService;
    }

    @GetMapping("/getEvents")
    public ResponseEntity<List<EventDto>> getEvents() {
        List<Event> events = eventService.getEvents();
        List<EventDto> eventDtos = new ArrayList<>();
        for (Event event : events) {
            String base64Img;
            if(event.getEventImg() != null) {
                base64Img = Base64.getEncoder().encodeToString(event.getEventImg());
            } else {
                base64Img = "null";
            }

            EventDto eventDto = new EventDto(
                    event.getId(), event.getEventName(), event.getDescription(),
                    event.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    event.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    event.getLatitude(), event.getLongitude(), base64Img,
                    event.getLocationDesc(), event.getCity().getZipCode());
            eventDtos.add(eventDto);
        }
        return ResponseEntity.ok(eventDtos);
    }

    @GetMapping("/getEventsLogs")
    public ResponseEntity<List<EventsLogDto>> getEventsLogs() {
        List<EventsLog> eventsLogs = eventService.getEventsLogs();
        List<EventsLogDto> eventsLogsDtos = new ArrayList<>();
        for (EventsLog eventsLog : eventsLogs) {
            EventsLogDto eventsLogDto = new EventsLogDto(eventsLog.getId(), eventsLog.getVolunteer().getId(), eventsLog.getEvent().getId(), eventsLog.getIsPresent(), eventsLog.getNote());
            eventsLogsDtos.add(eventsLogDto);
        }
        return ResponseEntity.ok(eventsLogsDtos);
    }

    @GetMapping("/downloadEventPdf/{idEvent}")
    public ResponseEntity<byte[]> downloadEventPdf(@PathVariable int idEvent) {
        try {
            Event event = eventService.getEventById(idEvent);
            if (event != null) {
                byte[] eventPdf = eventService.createEventPdf(event);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "event-" + event.getEventName() + ".pdf");
                return new ResponseEntity<>(eventPdf, headers, HttpStatus.OK);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/insertEvent")
    public ResponseEntity<Boolean> insertEvent(@RequestBody EventDto eventDto) {
        Event event = new Event(
                eventDto.getId(),
                eventDto.getEventName(),
                eventDto.getDescription(),
                LocalDateTime.parse(eventDto.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                LocalDateTime.parse(eventDto.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                eventDto.getLatitude(),
                eventDto.getLongitude(),
                Base64.getDecoder().decode(eventDto.getEventImg()),
                eventDto.getLocationDesc(),
                eventService.findCityByZipCode(eventDto.getCity())
        );
        return ResponseEntity.ok(eventService.insertEvent(event) == null);
    }

    @PostMapping("/insertLog")
    public ResponseEntity<Boolean> insertLog(@RequestBody EventsLogDto eventsLogDto) {
        try {
            EventsLog eventsLog = new EventsLog(
                    eventsLogDto.getId(),
                    volunteerService.getVolunteerById(eventsLogDto.getVolunteer()),
                    eventService.getEventById(eventsLogDto.getEvent()),
                    eventsLogDto.getIsPresent(),
                    eventsLogDto.getNote()
            );
            return ResponseEntity.ok(eventService.insertLog(eventsLog) == null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/markAsPresent")
    public ResponseEntity<Boolean> markAsPresent(@RequestBody EventsLogDto eventsLogDto) {
        try {
            return ResponseEntity.ok(eventService.markAsPresent(eventsLogDto) == null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/insertInitLogs")
    public ResponseEntity<Boolean> insertInitLogs(@RequestBody List<EventsLogDto> initLogsDtos) {
        try {
            List<EventsLog> initLogs = new ArrayList<>();
            for (EventsLogDto eventsLogDto : initLogsDtos) {
                initLogs.add(new EventsLog(
                        eventsLogDto.getId(), volunteerService.getVolunteerById(eventsLogDto.getVolunteer()),
                        eventService.getEventById(eventsLogDto.getEvent()), eventsLogDto.getIsPresent(), eventsLogDto.getNote()
                ));
            }
            List<EventsLog> result = eventService.insertInitLogs(initLogs);
            return ResponseEntity.ok( result != null && !result.isEmpty());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
