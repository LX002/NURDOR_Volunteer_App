package com.nurdorproject.event_service.utils;

import com.nurdorproject.event_service.dto.EventDto;
import com.nurdorproject.event_service.model.Event;

import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class EventMapper {

    public static EventDto mapToDto(Event e) {
        byte[] pic = e.getEventImg();
        return new EventDto(
                e.getId(), e.getEventName(), e.getDescription(),
                e.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                e.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                e.getLatitude(), e.getLongitude(), pic != null ? Base64.getEncoder().encodeToString(pic) : null,
                e.getLocationDesc(), e.getCity());
    }
}
