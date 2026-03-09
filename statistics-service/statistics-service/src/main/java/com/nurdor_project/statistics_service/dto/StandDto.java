package com.nurdor_project.statistics_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class StandDto implements Serializable {
    private final Integer id;
    @NotNull
    @Size(max = 100)
    private final String standName;
    @NotNull
    private final Integer donations;
    @NotNull
    private final Integer idEvent;
}