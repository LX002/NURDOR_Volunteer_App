package com.nurdor_project.events_log_service.repository;

import com.nurdor_project.events_log_service.model.EventsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventsLogRepository extends JpaRepository<EventsLog, Integer> {

    Optional<EventsLog> findByVolunteerAndEvent(Integer volunteer, Integer event);

    @Query("select e from EventsLog e where e.volunteer = :idV and e.event = :idE and e.note = 'initLog'")
    Optional<EventsLog> findInitLogByVolunteerAndEvent(@Param("idV") Integer idVolunteer, @Param("idE") Integer idEvent);

    @Query("select e.volunteer from EventsLog e where e.event = :idE")
    List<Integer> findVolunteerIdsByIdEvent(@Param("idE") Integer idEvent);

    @Query("select e from EventsLog e where e.isPresent = 1 and e.event in :ids")
    List<EventsLog> findEventsLogsByActiveEventsIds(@Param("ids") List<Integer> ids);

    Optional<EventsLog> findByVolunteerAndIsPresent(Integer volunteer, byte isPresent);

    @Modifying
    @Query("update EventsLog set isPresent = 0 where volunteer in :ids")
    void dismissVolunteers(@Param("ids") List<Integer> ids);
}