package com.nurdor_project.volunteer_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

@Data
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