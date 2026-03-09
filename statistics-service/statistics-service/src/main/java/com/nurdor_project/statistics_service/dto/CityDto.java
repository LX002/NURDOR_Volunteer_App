package com.nurdor_project.statistics_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class CityDto implements Serializable {
    @NotNull
    @Size(max = 10)
    private String zipCode;
    @NotNull
    @Size(max = 100)
    private String cityName;
}