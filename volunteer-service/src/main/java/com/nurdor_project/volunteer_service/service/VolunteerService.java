package com.nurdor_project.volunteer_service.service;

import com.nurdor_project.volunteer_service.dto.EventsLogDto;
import com.nurdor_project.volunteer_service.dto.PresentVolunteerEventDto;
import com.nurdor_project.volunteer_service.dto.VolunteerDto;
import com.nurdor_project.volunteer_service.exception.VolunteerNotFoundException;
import com.nurdor_project.volunteer_service.model.Volunteer;
import com.nurdor_project.volunteer_service.proxy.EventsLogProxy;
import com.nurdor_project.volunteer_service.repository.VolunteerRepository;
import com.nurdor_project.volunteer_service.utils.VolunteerMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class VolunteerService {

    private VolunteerRepository volunteerRepository;
    private EventsLogProxy eventsLogProxy;


    public List<Volunteer> findAll() {
        return volunteerRepository.findAll();
    }

    public List<Volunteer> findByIdEvent(Integer idEvent) {
        List<Integer> volunteerIds = eventsLogProxy.findVolunteerIds(idEvent);
        return volunteerRepository.findVolunteersByIds(volunteerIds);
    }

    public Volunteer findById(Integer id) {
        return volunteerRepository.findById(id).orElseThrow(() -> new VolunteerNotFoundException("Volunteer with id: " + id + " is not found!"));
    }

    // TODO: vrati listu celih EventsLogDto objekata mesto liste integera i grupisi ih u mapu <idEvent, listaVolontera>
    public Map<Integer, PresentVolunteerEventDto> groupPresentVolunteersByEvent() {
        List<EventsLogDto> eventsLogDtos = eventsLogProxy.findEventsLogsByActiveEventsIds();
        Map<Integer, ArrayList<Integer>> temp = new HashMap<>();
        Map<Integer, PresentVolunteerEventDto> result = new HashMap<>();

        eventsLogDtos.forEach(e -> {
            if(!temp.containsKey(e.getEvent())) {
                temp.put(e.getEvent(), new ArrayList<>());
            }
            temp.get(e.getEvent()).add(e.getVolunteer());
        });

        for(Integer idEvent: temp.keySet()) {
            List<Integer> value = temp.get(idEvent);
            result.put(idEvent, new PresentVolunteerEventDto(value.stream().map(idVolunteer -> VolunteerMapper.mapToDto(volunteerRepository.findVolunteerById(idVolunteer))).toList(), value.size()));
        }

        return result;
    }
}
