package com.rzk.nurdor.service;

import com.rzk.nurdor.controller.LoginController;
import com.rzk.nurdor.dto.VolunteerDto;
import com.rzk.nurdor.dto.VolunteerExpandedDto;
import com.rzk.nurdor.model.City;
import com.rzk.nurdor.model.Volunteer;
import com.rzk.nurdor.model.VolunteerRole;
import com.rzk.nurdor.repository.CityRepository;
import com.rzk.nurdor.repository.VolunteerRepository;
import com.rzk.nurdor.repository.VolunteerRoleRepository;
import com.rzk.nurdor.security.PasswordUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LoginService {

    VolunteerRepository volunteerRepository;
    CityRepository cityRepository;
    VolunteerRoleRepository volunteerRoleRepository;

    public LoginService(VolunteerRepository volunteerRepository,
                        CityRepository cityRepository,
                        VolunteerRoleRepository volunteerRoleRepository) {
        this.volunteerRepository = volunteerRepository;
        this.cityRepository = cityRepository;
        this.volunteerRoleRepository = volunteerRoleRepository;
    }

    public boolean authenticate(VolunteerDto volunteerDto) {
        Volunteer volunteer = volunteerRepository.findByUsername(volunteerDto.getUsername());
        return volunteer != null && PasswordUtils.verifyPassword(volunteerDto.getPassword(), volunteer.getPassword());
    }

    public List<City> getCities() {
        return cityRepository.findAll();
    }

    public List<VolunteerRole> getRoles() {
        return volunteerRoleRepository.findAll();
    }

    public Volunteer saveVolunteer(Volunteer volunteer) {
        return volunteerRepository.save(volunteer);
    }

    public City findCityByZipCode(String nearestCity) {
        return cityRepository.findById(nearestCity).orElseThrow();
    }

    public VolunteerRole findVolunteerRoleById(int volunteerRole) {
        return volunteerRoleRepository.findById(volunteerRole).orElseThrow();
    }
}
