package com.nurdor_project.events_log_service.service;

import com.nurdor_project.events_log_service.dto.EventDto;
import com.nurdor_project.events_log_service.dto.EventsLogDto;
import com.nurdor_project.events_log_service.exception.InvalidEventsLogException;
import com.nurdor_project.events_log_service.model.EventsLog;
import com.nurdor_project.events_log_service.proxy.EventProxy;
import com.nurdor_project.events_log_service.proxy.VolunteerProxy;
import com.nurdor_project.events_log_service.repository.EventsLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Integer> findVolunteerIdsByIdEvent(Integer idEvent) {
        return eventsLogRepository.findVolunteerIdsByIdEvent(idEvent);
    }

    public List<EventsLog> findEventsLogsByActiveEventsIds() {
        List<Integer> activeEventsIds = eventProxy.findStartedEvents()
                .stream()
                .map(EventDto::getId)
                .toList();
        return eventsLogRepository.findEventsLogsByActiveEventsIds(activeEventsIds);
    }

    public EventsLog insertLog(EventsLog eventsLog) {
        volunteerAndEventCheck(eventsLog.getVolunteer(), eventsLog.getEvent(), false);
        return eventsLogRepository.save(eventsLog);
    }

    @Transactional
    public EventsLog updatePresence(EventsLogDto eventsLogDto) {
        volunteerAndEventCheck(eventsLogDto.getVolunteer(), eventsLogDto.getEvent(), true);

        EventsLog eventsLog = eventsLogRepository
                .findInitLogByVolunteerAndEvent(eventsLogDto.getVolunteer(), eventsLogDto.getEvent())
                .orElseThrow();
        eventsLog.setIsPresent(eventsLogDto.getIsPresent());

        String note = eventsLogDto.getNote();
        if(note != null && !note.isBlank()) eventsLog.setNote(note);

        return eventsLogRepository.save(eventsLog);
    }

    private void volunteerAndEventCheck(int idVolunteer, int idEvent, boolean isUpdating) {
        if(volunteerProxy.findVolunteerById(idVolunteer) == null) {
            throw new InvalidEventsLogException("Invalid event's log: volunteer with id: " + idVolunteer  + " doesn't exist!");
        }
        if(eventProxy.findEventById(idEvent) == null) {
            throw new InvalidEventsLogException("Invalid event's log: event with id: " + idEvent  + " doesn't exist!");
        }

        if(!isUpdating) {
            EventsLog existingLog = eventsLogRepository.findByVolunteerAndEvent(idVolunteer, idEvent).orElse(null);
            if(existingLog != null) {
                throw new InvalidEventsLogException("Invalid event's log: log with idEvent: " + idEvent  + " and idVolunteer: " + idVolunteer + " already exists!");
            }
        }
    }
}
