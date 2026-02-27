package com.nurdor_project.volunteer_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
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
    @JsonProperty("profilePicture")
    String profilePicture;
    @JsonProperty("nearestCity")
    String nearestCity;
    @JsonProperty("volunteerRole")
    int volunteerRole;
}