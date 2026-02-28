package com.nurdorproject.event_service.controller;

import com.nurdorproject.event_service.dto.EventDto;
import com.nurdorproject.event_service.model.Event;
import com.nurdorproject.event_service.service.EventService;
import com.nurdorproject.event_service.utils.EventMapper;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
                .map(EventMapper::mapToDto).toList();
        return !events.isEmpty()
                ? ResponseEntity.ok(events)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/volunteer/events/findById/{idEvent}")
    public ResponseEntity<EventDto> findById(@PathVariable Integer idEvent) {
        return ResponseEntity.ok(EventMapper.mapToDto(eventService.findById(idEvent)));
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
}
