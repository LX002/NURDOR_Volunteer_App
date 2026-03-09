package com.rzk.nurdor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.rzk.nurdor.model.Volunteer}
 */
@Data
@AllArgsConstructor
public class VolunteerDto implements Serializable {
    @JsonProperty("username")
    String username;

    @JsonProperty("password")
    String password;
}