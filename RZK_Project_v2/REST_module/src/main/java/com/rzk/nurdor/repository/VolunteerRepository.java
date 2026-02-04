package com.rzk.nurdor.repository;

import com.rzk.nurdor.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerRepository extends JpaRepository<Volunteer, Integer> {

    Volunteer findByUsername(String username);
}