package com.nurdorproject.event_service.repository;

import com.nurdorproject.event_service.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {
}