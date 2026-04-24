package com.nurdor_project.events_log_service.config;

import com.nurdor_project.events_log_service.repository.EventsLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@AllArgsConstructor
public class DismissVolunteersJanitor {

    private EventsLogRepository eventsLogRepository;

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void dismissIdleVolunteers() {
        String idleLimit = LocalDateTime.now().minusMinutes(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try {
            eventsLogRepository.dismissInactiveVolunteers(idleLimit);
            System.out.println("Janitor: Cleaned up inactive volunteers.");
        } catch (Exception e) {
            System.out.println("Exception caught: " + e.getMessage());
        }
    }
}
