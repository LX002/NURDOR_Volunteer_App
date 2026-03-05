package com.nurdor_project.donations_service.controller;

import com.nurdor_project.donations_service.dto.StartEventDto;
import com.nurdor_project.donations_service.model.Stand;
import com.nurdor_project.donations_service.service.StandService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class StandController {

    private StandService standService;

    @PatchMapping("/volunteer/stands/donate/{idStand}")
    public ResponseEntity<String> donate(@PathVariable Integer idStand, @RequestParam("amount") Integer amount) {
        return ResponseEntity.ok(standService.donateToStand(amount, idStand));
    }

    @PatchMapping("/admin/stands/attachToEvent")
    public ResponseEntity<List<Stand>> attachStandsToEvent(@RequestBody @Valid StartEventDto startEventDto) {
        return ResponseEntity.ok(standService.tieStandsToEvent(startEventDto.getNumberOfStands(), startEventDto.getIdEvent()));
    }

    @PatchMapping("/admin/stands/detachFromEvent/{idEvent}")
    public ResponseEntity<List<Stand>> detachStandsFromEvent(@PathVariable Integer idEvent) {
        return ResponseEntity.ok(standService.tieStandsToEvent(0, idEvent));
    }
}
