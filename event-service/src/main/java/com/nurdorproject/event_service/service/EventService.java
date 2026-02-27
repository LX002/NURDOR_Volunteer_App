package com.nurdorproject.event_service.service;

import com.nurdorproject.event_service.model.Event;
import com.nurdorproject.event_service.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EventService {

    private EventRepository eventRepository;

    public List<Event> findAll() {
        return eventRepository.findAll();
    }
}
