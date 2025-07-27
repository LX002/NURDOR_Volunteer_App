package com.rzk.nurdor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "event", schema = "rzk_rma_schema")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEvent", nullable = false)
    private Integer id;

    @Column(name = "eventName", nullable = false, length = 100)
    private String eventName;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "startTime", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "endTime", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Lob
    @JoinColumn(name = "eventImg", nullable = true)
    private byte[] eventImg;

    @JoinColumn(name = "locationDesc", nullable = true)
    private String locationDesc;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "city", nullable = false)
    private City city;

    @JsonIgnore
    @OneToMany(mappedBy = "event")
    private Set<EventsLog> eventsLogs = new LinkedHashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "event")
    private Set<Stand> stands = new LinkedHashSet<>();

    public Event() {
        // default constructor...
    }

    public Event(Integer id, String eventName, String description, LocalDateTime startTime, LocalDateTime endTime, Double latitude, Double longitude, byte[] eventImg, String locationDesc, City city) {
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDateTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Set<EventsLog> getEventsLogs() {
        return eventsLogs;
    }

    public void setEventsLogs(Set<EventsLog> eventsLogs) {
        this.eventsLogs = eventsLogs;
    }

    public Set<Stand> getStands() {
        return stands;
    }

    public void setStands(Set<Stand> stands) {
        this.stands = stands;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public byte[] getEventImg() {
        return eventImg;
    }

    public void setEventImg(byte[] eventImg) {
        this.eventImg = eventImg;
    }

    public String getLocationDesc() {
        return locationDesc;
    }

    public void setLocationDesc(String locationDesc) {
        this.locationDesc = locationDesc;
    }
}