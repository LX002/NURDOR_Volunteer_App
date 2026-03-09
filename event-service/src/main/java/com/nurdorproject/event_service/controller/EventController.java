package com.nurdorproject.event_service.controller;

import com.nurdorproject.event_service.dto.*;
import com.nurdorproject.event_service.model.Event;
import com.nurdorproject.event_service.proxy.DonationsProxy;
import com.nurdorproject.event_service.service.EventService;
import com.nurdorproject.event_service.utils.EventMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class EventController {

    private EventService eventService;
    private DonationsProxy donationsProxy;

    @GetMapping("/volunteer/events/getEvents")
    public ResponseEntity<List<EventDto>> findAll() {
        List<EventDto> events = eventService.findAll().stream()
                .map(EventMapper::mapToDto).toList();
        return !events.isEmpty()
                ? ResponseEntity.ok(events)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/admin/events/finished")
    public ResponseEntity<List<Event>> findFinishedEvents() {
        return ResponseEntity.ok(eventService.findFinishedEvents());
    }

    @GetMapping("/volunteer/events/findById/{idEvent}")
    public ResponseEntity<EventDto> findById(@PathVariable Integer idEvent) {
        return ResponseEntity.ok(EventMapper.mapToDto(eventService.findById(idEvent)));
    }

    @GetMapping("/admin/events/started")
    public ResponseEntity<List<Event>> findStartedEvents() {
        return ResponseEntity.ok(eventService.findByIsStarted((byte) 1));
    }

    @GetMapping("/volunteer/events/getPdfById/{idEvent}")
    public ResponseEntity<byte[]> downloadEventPdf(@PathVariable int idEvent) {
        // check exceptions via exception handlers....
        try {
            Event event = eventService.findById(idEvent);
            byte[] eventPdf = eventService.createEventPdf(event);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                    ContentDisposition.attachment()
                            .filename("event-" + event.getEventName() + ".pdf")
                            .build()
            );
            return new ResponseEntity<>(eventPdf, headers, HttpStatus.OK);
        } catch (JRException jre) {
            System.out.println("Exception during generating pdf:\n" + jre.getMessage());
            jre.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/admin/events/start")
    public ResponseEntity<StartEventResultDto> startEvent(@RequestBody @Valid StartEventDto startEventDto) {
        int idEvent = startEventDto.getIdEvent();
        updateEvent(idEvent, (byte) 1, 0L);
        return ResponseEntity.ok(new StartEventResultDto(
                "Started event: " + idEvent,
                donationsProxy.attachStandsToEvent(startEventDto)
        ));
    }

    @PostMapping("/admin/events/end/{idEvent}")
    public ResponseEntity<EndEventResultDto> endEvent(@PathVariable Integer idEvent) {
        List<StandDto> stands = donationsProxy.detachStandsFromEvent(idEvent);
        long totalDonations = stands.stream().mapToLong(StandDto::getDonations).sum();
        updateEvent(idEvent, (byte) 0, totalDonations);
        return ResponseEntity.ok(new EndEventResultDto("Ended event: " + idEvent, totalDonations, stands));
    }

    private void updateEvent(int idEvent, byte isStarted, Long totalDonations) {
        Event event = eventService.findById(idEvent);
        event.setIsStarted(isStarted);
        event.setTotalDonations(totalDonations);
        eventService.save(event);
    }
}
