package com.nurdor_project.volunteer_service.utils;

import com.nurdor_project.volunteer_service.dto.VolunteerDto;
import com.nurdor_project.volunteer_service.model.Volunteer;
import org.springframework.stereotype.Component;

import java.util.Base64;

public class VolunteerMapper {

    public static VolunteerDto mapToDto(Volunteer volunteer) {
        byte[] pic = volunteer.getProfilePicture();
        return new VolunteerDto(
                volunteer.getId(),
                volunteer.getName(),
                volunteer.getSurname(),
                volunteer.getAddress(),
                volunteer.getPhoneNumber(),
                volunteer.getEmail(),
                volunteer.getUsername(),
                volunteer.getPassword(),
                pic != null ? Base64.getEncoder().encodeToString(pic) : null,
                volunteer.getNearestCity().getZipCode(),
                volunteer.getVolunteerRole().getId()
        );
    }
}
