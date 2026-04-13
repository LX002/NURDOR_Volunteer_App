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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public List<Stand> tieStandsToEvent(Integer numberOfStands, Integer idEvent) {
        if(numberOfStands != 0) {
            List<Integer> freeStandsIds = standRepository.findFreeStandsIds(PageRequest.of(0, numberOfStands)).toList();
            int freeStandsCount = freeStandsIds.size();

            if(numberOfStands > freeStandsCount)
                throw new NotEnoughStandsException("Not enough free stands! There are " + freeStandsCount + " stands left!");

            Integer affectedRows = standRepository.updateIdEventByStandIds(idEvent, freeStandsIds);
            return findTakenStands(idEvent);
        } else {
            List<Integer> takenStandsIds = standRepository.findTakenStandsIdsByIdEvent(idEvent);
            List<Stand> takenStands = new ArrayList<>(standRepository.findByIdEvent(idEvent));
            Integer affectedRows = standRepository.updateIdEventByStandIds(null, takenStandsIds);
            return takenStands;
        }
    }

    public List<Stand> findTakenStands(Integer idEvent) {
        return standRepository.findByIdEvent(idEvent);
    }

    public String donateToStand(Integer amount, Integer idStand, Integer idEvent) {
        EventDto eventDto = eventProxy.findById(idEvent);
        if(eventDto == null)
            return "ERROR:Event not found!";

        if(eventDto.getIsStarted() != (byte) 1)
            throw new NotValidStandIdException("ERROR:Cannot donate - event " + idEvent + " not started yet!");

        Stand stand = findById(idStand);
        List<Stand> takenStands = findTakenStands(idEvent);
        if(!takenStands.contains(stand))
            throw new NotValidStandIdException("ERROR:STAND_" + idStand + " is not being used in " + eventDto.getEventName());

        Integer currentAmount = stand.getDonations();
        stand.setDonations(currentAmount + amount);
        Stand savedStand = standRepository.save(stand);

        return "SUCCESS:Donated " + amount + "RSD on STAND_" + idStand + ". Total stand donations: " + savedStand.getDonations() + "RSD";
    }

    public List<Stand> findAll() {
        return standRepository.findAll();
    }
}
