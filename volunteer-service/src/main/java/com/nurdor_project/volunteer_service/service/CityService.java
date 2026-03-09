package com.nurdor_project.volunteer_service.service;

import com.nurdor_project.volunteer_service.model.City;
import com.nurdor_project.volunteer_service.repository.CityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CityService {

    private CityRepository cityRepository;

    public List<City> findAll() {
        return cityRepository.findAll();
    }
}
