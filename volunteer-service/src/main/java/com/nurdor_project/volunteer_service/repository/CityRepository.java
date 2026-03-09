package com.nurdor_project.volunteer_service.repository;

import com.nurdor_project.volunteer_service.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, String> {
}