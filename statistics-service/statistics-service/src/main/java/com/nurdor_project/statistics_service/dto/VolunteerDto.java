package com.nurdor_project.statistics_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class VolunteerDto implements Serializable {
    @JsonProperty("id")
    int id;
    @JsonProperty("name")
    String name;
    @JsonProperty("surname")
    String surname;
    @JsonProperty("address")
    String address;
    @JsonProperty("phoneNumber")
    String phoneNumber;
    @JsonProperty("email")
    String email;
    @JsonProperty("username")
    String username;
    @JsonProperty("password")
    String password;
    @JsonIgnore
    @JsonProperty("profilePicture")
    String profilePicture;
    @JsonProperty("nearestCity")
    String nearestCity;
    @JsonProperty("volunteerRole")
    Integer volunteerRole;
}