package com.nurdor_project.volunteer_service.repository;

import com.nurdor_project.volunteer_service.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Integer> {

    Optional<Volunteer> findByUsername(String username);

    Volunteer findVolunteerById(Integer id);

    @Query("select v from Volunteer v where v.id in :ids")
    List<Volunteer> findVolunteersByIds(@Param("ids") List<Integer> ids);
}