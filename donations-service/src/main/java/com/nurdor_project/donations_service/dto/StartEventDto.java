package com.nurdor_project.donations_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StartEventDto {

    @Min(1)
    private int idEvent;

    @Min(1)
    private int numberOfStands;
}
