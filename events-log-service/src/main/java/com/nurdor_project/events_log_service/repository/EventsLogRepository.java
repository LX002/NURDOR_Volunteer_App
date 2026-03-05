package com.nurdor_project.events_log_service.repository;

import com.nurdor_project.events_log_service.model.EventsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EventsLogRepository extends JpaRepository<EventsLog, Integer> {

    @Query("select e from EventsLog e where e.volunteer = :idVolunteer and e.event = :idEvent and e.note = 'initLog'")
    Optional<EventsLog> findInitLogByVolunteerAndEvent(int idVolunteer, int idEvent);
}