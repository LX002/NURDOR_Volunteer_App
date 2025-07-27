package com.rzk.nurdor.repository;

import com.rzk.nurdor.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {
}