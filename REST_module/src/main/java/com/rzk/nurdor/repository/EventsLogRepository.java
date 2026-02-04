package com.rzk.nurdor.repository;

import com.rzk.nurdor.model.Event;
import com.rzk.nurdor.model.EventsLog;
import com.rzk.nurdor.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EventsLogRepository extends JpaRepository<EventsLog, Integer> {
    @Query("select e from EventsLog e where e.volunteer.id = :idVolunteer and e.event.id = :idEvent and e.note = 'initLog'")
    Optional<EventsLog> findInitLogByVolunteerAndEvent(int idVolunteer, int idEvent);
}