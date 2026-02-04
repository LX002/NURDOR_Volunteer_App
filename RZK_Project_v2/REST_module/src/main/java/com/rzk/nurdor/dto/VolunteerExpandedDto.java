package com.rzk.nurdor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.rzk.nurdor.model.Volunteer}
 */
@Value
public class VolunteerExpandedDto implements Serializable {
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

    public VolunteerExpandedDto(int id, String name, String surname, String address, String phoneNumber, String email, String username, String password, String profilePicture, String nearestCity, int volunteerRole) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.username = username;
        this.password = password;
        this.profilePicture = profilePicture;
        this.nearestCity = nearestCity;
        this.volunteerRole = volunteerRole;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNearestCity() {
        return nearestCity;
    }

    public int getVolunteerRole() {
        return volunteerRole;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}