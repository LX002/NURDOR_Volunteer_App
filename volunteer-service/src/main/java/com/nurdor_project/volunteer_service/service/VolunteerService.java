package com.nurdor_project.volunteer_service.service;

import com.nurdor_project.volunteer_service.exception.VolunteerNotFoundException;
import com.nurdor_project.volunteer_service.model.Volunteer;
import com.nurdor_project.volunteer_service.repository.VolunteerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class VolunteerService {

    private VolunteerRepository volunteerRepository;

    public List<Volunteer> findAll() {
        return volunteerRepository.findAll();
    }

    public Volunteer findById(Integer id) {
        return volunteerRepository.findById(id).orElseThrow(() -> new VolunteerNotFoundException("Volunteer with id: " + id + " is not found!"));
    }
}
