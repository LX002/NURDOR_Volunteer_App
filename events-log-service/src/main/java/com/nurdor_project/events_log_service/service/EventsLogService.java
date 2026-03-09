package com.nurdor_project.events_log_service.service;

import com.nurdor_project.events_log_service.dto.EventDto;
import com.nurdor_project.events_log_service.dto.EventsLogDto;
import com.nurdor_project.events_log_service.exception.InvalidEventsLogException;
import com.nurdor_project.events_log_service.model.EventsLog;
import com.nurdor_project.events_log_service.proxy.EventProxy;
import com.nurdor_project.events_log_service.proxy.VolunteerProxy;
import com.nurdor_project.events_log_service.repository.EventsLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        volunteerAndEventCheck(eventsLog.getVolunteer(), eventsLog.getEvent(), eventsLog.getIsPresent(), false);
        return eventsLogRepository.save(eventsLog);
    }

    @Transactional
    public EventsLog updatePresence(EventsLogDto eventsLogDto) {
        volunteerAndEventCheck(eventsLogDto.getVolunteer(), eventsLogDto.getEvent(), eventsLogDto.getIsPresent(), true);

        EventsLog eventsLog = eventsLogRepository
                .findInitLogByVolunteerAndEvent(eventsLogDto.getVolunteer(), eventsLogDto.getEvent())
                .orElseThrow();
        eventsLog.setIsPresent(eventsLogDto.getIsPresent());

        String note = eventsLogDto.getNote();
        if(note != null && !note.isBlank()) eventsLog.setNote(note);

        return eventsLogRepository.save(eventsLog);
    }

    private void volunteerAndEventCheck(int idVolunteer, int idEvent, byte isPresent, boolean isUpdating) {
        if(volunteerProxy.findVolunteerById(idVolunteer) == null) {
            throw new InvalidEventsLogException("Invalid event's log: volunteer with id: " + idVolunteer  + " doesn't exist!");
        }

        EventDto eventDto = eventProxy.findEventById(idEvent);
        if(eventDto == null) {
            throw new InvalidEventsLogException("Invalid event's log: event with id: " + idEvent  + " doesn't exist!");
        }

        if(isPresent == (byte) 1) {
            EventsLog logByVolunteer = eventsLogRepository.findByVolunteerAndIsPresent(idVolunteer, isPresent).orElse(null);
            if(logByVolunteer != null) {
                throw new InvalidEventsLogException("Invalid event's log: the volunteer with id: " + idVolunteer + " is already present on some other event, with id: " + logByVolunteer.getEvent());
            }
            if(eventDto.getIsStarted() == (byte) 0) {
                throw new InvalidEventsLogException("Invalid event's log: can't join to the event with id: " + idEvent + " hasn't started yet!");
            }
        }

        if(!isUpdating) {
            EventsLog existingLog = eventsLogRepository.findByVolunteerAndEvent(idVolunteer, idEvent).orElse(null);
            if(existingLog != null) {
                throw new InvalidEventsLogException("Invalid event's log: log with idEvent: " + idEvent  + " and idVolunteer: " + idVolunteer + " already exists!");
            }
        }
    }

    @Transactional
    public String dismissVolunteers(Integer idEvent) {
        List<Integer> volunteerIds = eventsLogRepository.findVolunteerIdsByIdEvent(idEvent);

        if(volunteerIds.isEmpty())
            return "204:No volunteers have picked event with id: " + idEvent;

        eventsLogRepository.dismissVolunteers(volunteerIds);
        return "200:Volunteers are dimissed from event with id: " + idEvent;
    }
}
