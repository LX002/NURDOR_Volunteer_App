package com.nurdor_project.events_log_service.service;

import com.nurdor_project.events_log_service.exception.InvalidEventsLogException;
import com.nurdor_project.events_log_service.model.EventsLog;
import com.nurdor_project.events_log_service.proxy.EventProxy;
import com.nurdor_project.events_log_service.proxy.VolunteerProxy;
import com.nurdor_project.events_log_service.repository.EventsLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EventsLogService {

    private EventsLogRepository eventsLogRepository;
    private EventProxy eventProxy;
    private VolunteerProxy volunteerProxy;

    public List<EventsLog> findAll() {
        return eventsLogRepository.findAll();
    }

    public EventsLog insertLog(EventsLog eventsLog) {
        int idVolunteer = eventsLog.getVolunteer();
        if(volunteerProxy.findVolunteerById(idVolunteer) == null) {
            throw new InvalidEventsLogException("Invalid event's log: volunteer with id: " + idVolunteer  + " doesn't exist!");
        }

        int idEvent = eventsLog.getEvent();
        if(eventProxy.findEventById(idEvent) == null) {
            throw new InvalidEventsLogException("Invalid event's log: event with id: " + idEvent  + " doesn't exist!");
        }

        return eventsLogRepository.save(eventsLog);
    }
}
