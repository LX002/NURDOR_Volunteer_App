package com.nurdorproject.event_service.utils;

import com.nurdorproject.event_service.dto.CreateEventDto;
import com.nurdorproject.event_service.dto.EventDto;
import com.nurdorproject.event_service.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class EventMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter mobileFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static EventDto mapToDto(Event e) {
        byte[] pic = e.getEventImg();
        String encodedPic = pic != null ? Base64.getEncoder().encodeToString(pic) : null;
        return new EventDto(
                e.getId(), e.getEventName(), e.getDescription(), e.getIsStarted(),
                e.getStartTime().format(formatter),
                e.getEndTime().format(formatter),
                e.getLatitude(), e.getLongitude(), encodedPic,
                e.getLocationDesc(), e.getTotalDonations(), e.getCity());
    }

    public static Event mapToEvent(EventDto e) {
        String pic = e.getEventImg();
        byte[] decodedPic = pic != null ? Base64.getDecoder().decode(pic) : null;
        return new Event(
                e.getId(), e.getEventName(), e.getDescription(), e.getIsStarted(),
                LocalDateTime.parse(e.getStartTime(), mobileFormatter),
                LocalDateTime.parse(e.getEndTime(), mobileFormatter),
                e.getLatitude(), e.getLongitude(), e.getLocationDesc(),
                decodedPic, e.getTotalDonations(), e.getCity());
    }

    public static Event mapToEventWithoutId(CreateEventDto e) {
        String pic = e.getEventImg();
        byte[] decodedPic = pic != null ? Base64.getDecoder().decode(pic) : null;
        Event event = new Event();
        event.setEventName(e.getEventName());
        event.setDescription(e.getDescription());
        event.setTotalDonations(e.getTotalDonations());
        event.setIsStarted(e.getIsStarted());
        event.setEventImg(decodedPic);
        event.setCity(e.getCity());
        event.setStartTime(LocalDateTime.parse(e.getStartTime(), mobileFormatter));
        event.setEndTime(LocalDateTime.parse(e.getEndTime(), mobileFormatter));
        event.setLocationDesc(e.getLocationDesc());
        event.setLatitude(e.getLatitude());
        event.setLongitude(e.getLongitude());
        return event;
    }
}
