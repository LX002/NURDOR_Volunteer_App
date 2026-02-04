package com.rzk.nurdor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.rzk.nurdor.model.VolunteerRole}
 */
@Value
public class VolunteerRoleDto implements Serializable {
    @JsonProperty("id")
    Integer id;
    @JsonProperty("roleName")
    String roleName;

    public Integer getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }
}