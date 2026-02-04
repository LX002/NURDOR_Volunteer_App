package com.rzk.nurdor.controller;

import com.rzk.nurdor.dto.VolunteerExpandedDto;
import com.rzk.nurdor.model.Volunteer;
import com.rzk.nurdor.service.VolunteerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/volunteers")
public class VolunteerController {

    VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @GetMapping("/getVolunteers")
    public ResponseEntity<List<VolunteerExpandedDto>> getVolunteers() {
        List<Volunteer> volunteers = volunteerService.getAllVolunteers();
        List<VolunteerExpandedDto> volunteerDtos = new ArrayList<>();
        for(Volunteer v : volunteers) {

            volunteerDtos.add(new VolunteerExpandedDto(v.getId(), v.getName(), v.getSurname(),
                              v.getAddress(), v.getPhoneNumber(), v.getEmail(), v.getUsername(),
                              v.getPassword(), Base64.getEncoder().encodeToString(v.getProfilePicture()),
                              v.getNearestCity().getZipCode(), v.getVolunteerRole().getId()));
        }
        return ResponseEntity.ok(volunteerDtos);
    }
}
