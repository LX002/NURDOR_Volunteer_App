package com.nurdor_project.volunteer_service.dto;

import com.nurdor_project.volunteer_service.model.Volunteer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PresentVolunteerEventDto {

    private List<VolunteerDto> volunteers;
    private Integer count;
}
