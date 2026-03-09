package com.nurdor_project.donations_service.controller;

import com.nurdor_project.donations_service.dto.DonationDto;
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

    @GetMapping("/volunteer/findByIdEvent/{idEvent}")
    public ResponseEntity<List<Stand>> findByIdEvent(@PathVariable Integer idEvent) {
        return ResponseEntity.ok(standService.findTakenStands(idEvent));
    }

    @PatchMapping("/volunteer/stands/addDonation")
    public ResponseEntity<String> donate(@RequestBody @Valid DonationDto donationDto) {
        return ResponseEntity.ok(standService.donateToStand(donationDto.getAmount(), donationDto.getIdStand(), donationDto.getIdEvent()));
    }

    @PostMapping("/admin/stands/attachToEvent")
    public ResponseEntity<List<Stand>> attachStandsToEvent(@RequestBody @Valid StartEventDto startEventDto) {
        return ResponseEntity.ok(standService.tieStandsToEvent(startEventDto.getNumberOfStands(), startEventDto.getIdEvent()));
    }

    @PostMapping("/admin/stands/detachFromEvent/{idEvent}")
    public ResponseEntity<List<Stand>> detachStandsFromEvent(@PathVariable Integer idEvent) {
        return ResponseEntity.ok(standService.tieStandsToEvent(0, idEvent));
    }
}
