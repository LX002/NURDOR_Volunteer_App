package com.nurdor_project.events_log_service.repository;

import com.nurdor_project.events_log_service.model.EventsLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventsLogRepository extends JpaRepository<EventsLog, Integer> {
}