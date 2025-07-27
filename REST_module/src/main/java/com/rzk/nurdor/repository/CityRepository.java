package com.rzk.nurdor.repository;

import com.rzk.nurdor.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, String> {
}