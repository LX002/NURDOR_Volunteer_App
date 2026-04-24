package com.nurdor_project.events_log_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePresenceDto {
    @JsonProperty("volunteer")
    private final Integer volunteer;
    @JsonProperty("event")
    private final Integer event;
    @JsonProperty("isPresent")
    private final Byte isPresent;
    @JsonProperty("note")
    private final String note;

}
