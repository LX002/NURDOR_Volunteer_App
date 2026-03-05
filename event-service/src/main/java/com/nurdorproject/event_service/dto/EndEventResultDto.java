package com.nurdorproject.event_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EndEventResultDto {

    private String message;
    private Integer totalDonations;
    private List<StandDto> stands;
}
