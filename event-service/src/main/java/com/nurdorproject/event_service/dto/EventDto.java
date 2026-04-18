package com.nurdorproject.event_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@Getter
@AllArgsConstructor
public class EventDto {
    @Min(1)
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
    @JsonProperty("eventImg")
    String eventImg;
    @JsonProperty("locationDesc")
    String locationDesc;
    @JsonProperty("totalDonations")
    Long totalDonations;
    @JsonProperty("city")
    String city;
}
