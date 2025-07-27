package com.rzk.nurdor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link EventsLog}
 */
@Value
public class EventsLogDto implements Serializable {
    @JsonProperty("id")
    int id;
    @JsonProperty("volunteer")
    int volunteer;
    @JsonProperty("event")
    int event;
    @JsonProperty("isPresent")
    byte isPresent;
    @JsonProperty("note")
    String note;

    public EventsLogDto(int id, int volunteer, int event, byte isPresent, String note) {
        this.id = id;
        this.volunteer = volunteer;
        this.event = event;
        this.isPresent = isPresent;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public int getVolunteer() {
        return volunteer;
    }

    public int getEvent() {
        return event;
    }

    public byte getIsPresent() {
        return isPresent;
    }

    public String getNote() {
        return note;
    }
}