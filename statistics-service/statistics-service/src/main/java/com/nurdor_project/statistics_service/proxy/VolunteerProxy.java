package com.nurdor_project.statistics_service.proxy;

import com.nurdor_project.statistics_service.dto.CityDto;
import com.nurdor_project.statistics_service.dto.PresentVolunteerEventDto;
import com.nurdor_project.statistics_service.dto.VolunteerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "volunteer-service")
public interface VolunteerProxy {

    @GetMapping("/api/volunteer/volunteers/findAll")
    List<VolunteerDto> findAll();

    @GetMapping("/api/volunteer/volunteers/cities")
    List<CityDto> findAllCities();

    @GetMapping("/api/admin/volunteers/findByEvent/{idEvent}")
    List<VolunteerDto> findByIdEvent(@PathVariable Integer idEvent);

    @GetMapping("/api/admin/volunteers/groupByEvent")
    Map<Integer, PresentVolunteerEventDto> groupPresentVolunteersByEvent();
}
