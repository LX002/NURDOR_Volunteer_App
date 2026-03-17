package com.nurdor_project.volunteer_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class CityDto implements Serializable {
    @Size(max = 10)
    private final String zipCode;
    @NotNull
    @Size(max = 100)
    private final String cityName;
}