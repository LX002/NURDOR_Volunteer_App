package com.nurdor_project.volunteer_service.repository;

import com.nurdor_project.volunteer_service.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Integer> {

    Optional<Volunteer> findByUsername(String username);
}