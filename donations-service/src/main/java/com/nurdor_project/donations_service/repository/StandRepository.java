package com.nurdor_project.donations_service.repository;

import com.nurdor_project.donations_service.model.Stand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StandRepository extends JpaRepository<Stand, Integer> {

    List<Stand> findByIdEvent(Integer idEvent);

    @Query("select s from Stand s where s.idEvent is null")
    Page<Stand> findFreeStands(Pageable pageable);
}