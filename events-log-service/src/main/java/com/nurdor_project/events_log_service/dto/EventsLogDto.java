package com.nurdor_project.events_log_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class EventsLogDto implements Serializable {

    @Min(1)
    @NotNull
    private final Integer volunteer;

    @Min(1)
    @NotNull
    private final Integer event;

    @Min(1)
    @NotNull
    private final Byte isPresent;

    private final String note;
}