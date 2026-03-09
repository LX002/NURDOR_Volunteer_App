package com.nurdor_project.statistics_service.proxy;

import com.nurdor_project.statistics_service.dto.StandDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "donations-service")
public interface DonationsProxy {

    @GetMapping("/api/volunteer/findByIdEvent/{idEvent}")
    List<StandDto> findByIdEvent(@PathVariable Integer idEvent);
}
