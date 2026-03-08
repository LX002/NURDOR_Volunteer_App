package com.nurdor_project.donations_service.service;

import com.nurdor_project.donations_service.dto.EventDto;
import com.nurdor_project.donations_service.exception.NotEnoughStandsException;
import com.nurdor_project.donations_service.exception.NotValidStandIdException;
import com.nurdor_project.donations_service.exception.StandNotFoundException;
import com.nurdor_project.donations_service.model.Stand;
import com.nurdor_project.donations_service.proxy.EventProxy;
import com.nurdor_project.donations_service.repository.StandRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class StandService {

    private StandRepository standRepository;
    private EventProxy eventProxy;

    public Stand findById(Integer id) {
        return standRepository.findById(id).orElseThrow(() -> new StandNotFoundException("Stand not found for id: " + id));
    }

    public List<Stand> tieStandsToEvent(Integer numberOfStands, Integer idEvent) {
        List<Stand> standsToSave = new ArrayList<>();

        if(numberOfStands != 0) {
            List<Stand> freeStands = standRepository.findFreeStands(PageRequest.of(0, numberOfStands)).toList();
            int freeStandsCount = freeStands.size();

            if(numberOfStands > freeStandsCount)
                throw new NotEnoughStandsException("Not enough free stands! There are " + freeStandsCount + " stands left!");

            for(Stand s: freeStands) {
                s.setIdEvent(idEvent);
                s.setDonations(0);
                standsToSave.add(s);
            }
            return standRepository.saveAll(standsToSave);
        } else {
            standsToSave = findTakenStands(idEvent);
            System.out.println("stands to save: " + standsToSave);
            List<Stand> takenStands = standsToSave.stream()
                    .map(s -> new Stand(s.getId(), s.getStandName(), s.getDonations(), s.getIdEvent()))
                    .toList();
            for(Stand s: standsToSave) {
                s.setIdEvent(null);
                s.setDonations(0);
            }
            standRepository.saveAll(standsToSave);
            return takenStands;
        }
    }

    public List<Stand> findTakenStands(Integer idEvent) {
        return standRepository.findByIdEvent(idEvent);
    }

    public String donateToStand(Integer amount, Integer idStand, Integer idEvent) {
        EventDto eventDto = eventProxy.findById(idEvent);
        if(eventDto == null)
            return "Event not found!";

        if(eventDto.getIsStarted() != (byte) 1)
            throw new NotValidStandIdException("Cannot donate - event " + idEvent + " not started yet!");

        Stand stand = findById(idStand);
        List<Stand> takenStands = findTakenStands(idEvent);
        if(!takenStands.contains(stand))
            throw new NotValidStandIdException("STAND_" + idStand + " is not being used in " + eventDto.getEventName());

        Integer currentAmount = stand.getDonations();
        stand.setDonations(currentAmount + amount);
        Stand savedStand = standRepository.save(stand);

        return "Donated " + amount + "RSD on STAND_" + idStand + ". Total stand donations: " + savedStand.getDonations() + "RSD";
    }
}
