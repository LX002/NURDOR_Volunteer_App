package com.nurdor_project.donations_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DonationDto {

    @Min(1)
    @NotNull
    private Integer amount;

    @Min(1)
    @NotNull
    private Integer idEvent;

    @Min(1)
    @NotNull
    private Integer idStand;
}
