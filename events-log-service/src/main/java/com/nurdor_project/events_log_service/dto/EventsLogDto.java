package com.nurdor_project.events_log_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class EventsLogDto implements Serializable {
    @NotNull
    private final Integer volunteer;
    @NotNull
    private final Integer event;
    @NotNull
    private final Byte isPresent;
    private final String note;
}