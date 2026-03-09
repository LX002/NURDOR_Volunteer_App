package com.nurdorproject.event_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@Getter
@AllArgsConstructor
public class EventDto {
    @JsonProperty("id")
    int id;
    @JsonProperty("eventName")
    String eventName;
    @JsonProperty("description")
    String description;
    @JsonProperty("isStarted")
    Byte isStarted;
    @JsonProperty("startTime")
    String startTime;
    @JsonProperty("endTime")
    String endTime;
    @JsonProperty("latitude")
    double latitude;
    @JsonProperty("longitude")
    double longitude;
    @JsonIgnore
    @JsonProperty("eventImg")
    String eventImg;
    @JsonProperty("locationDesc")
    String locationDesc;
    @JsonProperty("totalDonations")
    Long totalDonations;
    @JsonProperty("city")
    String city;
}
