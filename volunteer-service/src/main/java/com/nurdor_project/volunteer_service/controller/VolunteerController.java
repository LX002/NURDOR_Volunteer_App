package com.nurdor_project.volunteer_service.controller;

import com.nurdor_project.volunteer_service.VolunteerDto;
import com.nurdor_project.volunteer_service.model.Volunteer;
import com.nurdor_project.volunteer_service.service.VolunteerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/")
public class VolunteerController {

    private VolunteerService volunteerService;

    @GetMapping("/admin/volunteers/findAll")
    public ResponseEntity<List<Volunteer>> findAll() {
        List<VolunteerDto> volunteerDtos = volunteerService.findAll().stream()
                .map(v -> {
                    byte[] pic = v.getProfilePicture();
                    return new VolunteerDto(
                        v.getId(),
                        v.getName(),
                        v.getSurname(),
                        v.getAddress(),
                        v.getPhoneNumber(),
                        v.getEmail(),
                        v.getUsername(),
                        v.getPassword(),
                        pic != null ? Base64.getEncoder().encodeToString(v.getProfilePicture()) : null,
                        v.getNearestCity().getZipCode(),
                        v.getVolunteerRole().getId()
                    );
                }).toList();

        return !volunteerDtos.isEmpty()
                ? ResponseEntity.ok(volunteerService.findAll())
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
