package com.rzk.nurdor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "city", schema = "rzk_rma_schema")
public class City {
    @Id
    @Column(name = "zipCode", nullable = false, length = 10)
    private String zipCode;

    @Column(name = "cityName", nullable = false, length = 100)
    private String cityName;

    @JsonIgnore
    @OneToMany(mappedBy = "city")
    private Set<Event> events = new LinkedHashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "nearestCity")
    private Set<Volunteer> volunteers = new LinkedHashSet<>();

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public Set<Volunteer> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(Set<Volunteer> volunteers) {
        this.volunteers = volunteers;
    }
}