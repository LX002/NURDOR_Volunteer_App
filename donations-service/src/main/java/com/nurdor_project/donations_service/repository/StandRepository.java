package com.nurdor_project.donations_service.repository;

import com.nurdor_project.donations_service.model.Stand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StandRepository extends JpaRepository<Stand, Integer> {

    List<Stand> findByIdEvent(Integer idEvent);

    @Query("select s from Stand s where s.id IN (:ids)")
    List<Stand> findByStandIds(@Param("ids") List<Integer> ids);

    @Query("select s.id from Stand s where s.idEvent is null")
    Page<Integer> findFreeStandsIds(Pageable pageable);

    @Query("select s.id from Stand s where s.idEvent = :idEvent")
    List<Integer> findTakenStandsIdsByIdEvent(@Param("idEvent") Integer event);

    @Modifying
    @Query("update Stand s set s.idEvent = :idEvent, s.donations = 0 where s.id in (:standIds)")
    Integer updateIdEventByStandIds(@Param("idEvent") Integer idEvent, @Param("standIds") List<Integer> standIds);
}