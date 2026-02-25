package com.nurdorproject.api_gateway_v2.repository;

import com.nurdorproject.api_gateway_v2.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Integer> {

    Optional<Volunteer> findByUsername(String username);
}