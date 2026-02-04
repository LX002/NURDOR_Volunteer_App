package com.rzk.nurdor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.rzk.nurdor.model.Volunteer}
 */
@Value
public class VolunteerDto implements Serializable {
    @JsonProperty("username")
    String username;

    @JsonProperty("password")
    String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}