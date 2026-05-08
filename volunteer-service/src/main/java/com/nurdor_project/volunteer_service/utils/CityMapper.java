package com.nurdor_project.volunteer_service.utils;

import com.nurdor_project.volunteer_service.dto.CityDto;
import com.nurdor_project.volunteer_service.model.City;

public class CityMapper {

    public static CityDto mapToDto(City c) {
        return new CityDto(c.getZipCode(), c.getCityName());
    }
}
