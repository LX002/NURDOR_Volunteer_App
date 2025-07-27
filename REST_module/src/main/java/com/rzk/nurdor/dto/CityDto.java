package com.rzk.nurdor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.rzk.nurdor.model.City}
 */
@Value
public class CityDto implements Serializable {
    @JsonProperty("zipCode")
    String zipCode;
    @JsonProperty("cityName")
    String cityName;

    public String getZipCode() {
        return zipCode;
    }

    public String getCityName() {
        return cityName;
    }
}