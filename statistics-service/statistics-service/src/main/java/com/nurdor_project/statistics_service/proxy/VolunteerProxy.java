package com.nurdor_project.statistics_service.proxy;

import com.nurdor_project.statistics_service.dto.CityDto;
import com.nurdor_project.statistics_service.dto.VolunteerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "volunteer-service")
public interface VolunteerProxy {

    @GetMapping("/api/volunteer/volunteers/findAll")
    List<VolunteerDto> findAll();

    @GetMapping("/api/volunteer/volunteers/cities")
    List<CityDto> findAllCities();
}
