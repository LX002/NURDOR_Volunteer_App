package com.nurdorproject.auth_service.repository;

import com.nurdorproject.auth_service.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Integer> {

    Optional<Volunteer> findByUsername(String username);
}