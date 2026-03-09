package com.nurdor_project.events_log_service.utils;

import com.nurdor_project.events_log_service.dto.EventsLogDto;
import com.nurdor_project.events_log_service.model.EventsLog;

public class EventsLogMapper {

    public static EventsLog mapToEntity(EventsLogDto e) {
        return new EventsLog(e.getVolunteer(), e.getEvent(), e.getIsPresent(), e.getNote());
    }
}
