package com.nurdorproject.event_service.repository;

import com.nurdorproject.event_service.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {

    @Query("select e from Event e where e.endTime <= :now  and e.isStarted = 0 and e.totalDonations > 0")
    List<Event> findFinishedEvents(@Param("now") LocalDateTime now);
}