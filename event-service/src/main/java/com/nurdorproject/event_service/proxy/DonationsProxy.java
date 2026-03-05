package com.nurdorproject.event_service.proxy;

import com.nurdorproject.event_service.dto.StandDto;
import com.nurdorproject.event_service.dto.StartEventDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "donations-service")
public interface DonationsProxy {

    @PatchMapping("/api/admin/stands/attachToEvent")
    List<StandDto> attachStandsToEvent(@RequestBody @Valid StartEventDto startEventDto);

    @PatchMapping("/api/admin/stands/detachFromEvent/{idEvent}")
    List<StandDto> detachStandsFromEvent(@PathVariable Integer idEvent);
}
