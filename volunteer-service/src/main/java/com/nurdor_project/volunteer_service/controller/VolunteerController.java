package com.nurdor_project.volunteer_service.controller;

import com.nurdor_project.volunteer_service.dto.VolunteerDto;
import com.nurdor_project.volunteer_service.model.Volunteer;
import com.nurdor_project.volunteer_service.service.VolunteerService;
import com.nurdor_project.volunteer_service.utils.VolunteerMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class VolunteerController {

    private VolunteerService volunteerService;

    @GetMapping("/admin/volunteers/findAll")
    public ResponseEntity<List<Volunteer>> findAll() {
        List<VolunteerDto> volunteerDtos = volunteerService.findAll().stream()
                .map(VolunteerMapper::mapToDto)
                .toList();

        return !volunteerDtos.isEmpty()
                ? ResponseEntity.ok(volunteerService.findAll())
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/volunteer/volunteers/findById/{idVolunteer}")
    public ResponseEntity<VolunteerDto> findById(@PathVariable Integer idVolunteer) {
        VolunteerDto volunteerDto = VolunteerMapper.mapToDto(volunteerService.findById(idVolunteer));
        return ResponseEntity.ok(volunteerDto);
    }
}
