package com.nurdor_project.statistics_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class VolunteersEventDto {

    private String eventName;
    private Integer volunteerCount;
    private List<VolunteerDto> volunteers;
}
