package com.nurdor_project.donations_service.service;

import com.nurdor_project.donations_service.exception.NotValidStandIdException;
import com.nurdor_project.donations_service.exception.StandNotFoundException;
import com.nurdor_project.donations_service.model.Stand;
import com.nurdor_project.donations_service.repository.StandRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class StandService {

    private StandRepository standRepository;

    public Stand findById(Integer id) {
        return standRepository.findById(id).orElseThrow(() -> new StandNotFoundException("Stand not found for id: " + id));
    }

    public List<Stand> tieStandsToEvent(Integer numberOfStands, Integer idEvent) {
        List<Stand> standsToSave = new ArrayList<>();
        if(idEvent != null) {
            for(int i = 1; i <= numberOfStands; i++) {
                Stand stand = standRepository.findById(i).orElse(null);
                stand.setIdEvent(idEvent);
                stand.setDonations(0);
                standsToSave.add(stand);
            }
        } else {
            standsToSave = findTakenStands();
            for(Stand s: standsToSave) {
                s.setIdEvent(idEvent);
            }
        }
        return standRepository.saveAll(standsToSave);
    }

    public List<Stand> findTakenStands() {
        return standRepository.findByIdEvent(null);
    }

    public String donateToStand(Integer amount, Integer idStand) {
        Integer takenStandsCount = findTakenStands().size();

        if(takenStandsCount < 1 || idStand > takenStandsCount)
            throw new NotValidStandIdException("STAND_" + idStand + " is not being used!");

        Stand stand = findById(idStand);
        Integer currentAmount = stand.getDonations();
        stand.setDonations(currentAmount + amount);
        Stand savedStand = standRepository.save(stand);

        return "Donated " + amount + "RSD on STAND_" + idStand + ". Total stand donations: " + savedStand.getDonations() + "RSD";
    }
}
