package com.nurdor_project.statistics_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TotalDonationsDto {

    private String groupType;
    private Long totalDonations;
    private List<EventDto> events;
}
