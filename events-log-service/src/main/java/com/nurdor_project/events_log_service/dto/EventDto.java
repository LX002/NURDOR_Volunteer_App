package com.nurdor_project.events_log_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventDto {
    @JsonProperty("id")
    int id;
    @JsonProperty("eventName")
    String eventName;
    @JsonProperty("description")
    String description;
    @JsonProperty("startTime")
    String startTime;
    @JsonProperty("endTime")
    String endTime;
    @JsonProperty("latitude")
    double latitude;
    @JsonProperty("longitude")
    double longitude;
    @JsonProperty("eventImg")
    String eventImg;
    @JsonProperty("locationDesc")
    String locationDesc;
    @JsonProperty("city")
    String city;
}
