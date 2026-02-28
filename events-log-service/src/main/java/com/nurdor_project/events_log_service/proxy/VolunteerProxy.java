package com.nurdor_project.events_log_service.proxy;

import com.nurdor_project.events_log_service.dto.VolunteerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "volunteer-service")
public interface VolunteerProxy {

    @GetMapping("/api/volunteer/volunteers/findById/{idVolunteer}")
    ResponseEntity<VolunteerDto> findVolunteerById(@PathVariable Integer idVolunteer);
}
