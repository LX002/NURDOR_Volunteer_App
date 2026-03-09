package com.nurdor_project.statistics_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PresentVolunteerEventDto {

    private List<VolunteerDto> volunteers;
    private Integer count;
}
