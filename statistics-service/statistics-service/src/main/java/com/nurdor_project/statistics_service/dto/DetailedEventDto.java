package com.nurdor_project.statistics_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailedEventDto {

    private EventDto event;
    private List<StandDto> stands;
}
