package com.nurdor_project.donations_service.repository;

import com.nurdor_project.donations_service.model.Stand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StandRepository extends JpaRepository<Stand, Integer> {

    //@Query("select s from Stand s where s.idEvent is not null ")
    List<Stand> findByIdEvent(Integer idEvent);
}