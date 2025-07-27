package com.rzk.nurdor.service;

import com.rzk.nurdor.model.Volunteer;
import com.rzk.nurdor.repository.VolunteerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolunteerService {

    VolunteerRepository volunteerRepository;

    public VolunteerService(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }

    public List<Volunteer> getAllVolunteers() {
        return volunteerRepository.findAll();
    }

    public Volunteer getVolunteerById(int id) {
        return volunteerRepository.findById(id).orElseThrow();
    }
}
