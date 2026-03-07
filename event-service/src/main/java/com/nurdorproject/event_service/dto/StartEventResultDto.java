package com.nurdorproject.event_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StartEventResultDto {
    String message;
    List<StandDto> stands;
}
