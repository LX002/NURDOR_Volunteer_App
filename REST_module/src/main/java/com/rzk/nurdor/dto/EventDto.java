package com.rzk.nurdor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.io.Serializable;

@Value
public class EventDto implements Serializable {
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

    public EventDto(int id, String eventName, String description, String startTime, String endTime, double latitude, double longitude, String eventImg, String locationDesc, String city) {
        this.id = id;
        this.eventName = eventName;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.eventImg = eventImg;
        this.locationDesc = locationDesc;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public String getEventName() {
        return eventName;
    }

    public String getDescription() {
        return description;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCity() {
        return city;
    }

    public String getLocationDesc() {
        return locationDesc;
    }

    public String getEventImg() {
        return eventImg;
    }
}
