package com.nurdor_project.statistics_service.controller;

import com.nurdor_project.statistics_service.dto.CityDto;
import com.nurdor_project.statistics_service.dto.EventDto;
import com.nurdor_project.statistics_service.dto.TotalDonationsDto;
import com.nurdor_project.statistics_service.exception.InvalidGroupTypeException;
import com.nurdor_project.statistics_service.proxy.EventProxy;
import com.nurdor_project.statistics_service.proxy.VolunteerProxy;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/statistics")
public class StatisticsController {

    /* TODO add following stuff here
    * ukupne donacije za sve zavrsene eventove (uzima se danasnji datetime kao gornja granica zavrsetka dogadjaja) (event-service, dodaj totalDonations kolonu u event)
    * volonteri po (najblizim) gradovima
    * broj prijavljenih volontera za odredjeni dogadjaj
    * broj prisutnih volontera za odredjeni dogadjaj koji je u toku
    * broj tekucih dogadjaja, njihovo izlistavanje zajedno sa standovima? (koristi HATEOAS)
    * mozda da iskoristis hateoas na ostalim?
    * */

    private VolunteerProxy volunteerProxy;
    private EventProxy eventProxy;

    // TODO: test this!!! do the rest of functionalities!!!
    @GetMapping("/totalDonations/{groupType}")
    public ResponseEntity<List<TotalDonationsDto>> calculateTotalDonations(@PathVariable String groupType) {
        List<EventDto> events = eventProxy.findFinishedEvents();
        List<CityDto> cities = volunteerProxy.findAllCities();
        List<TotalDonationsDto> totalDonationsDtos = new ArrayList<>();
        long totalEventsDonations;

        switch(groupType) {
            case "all" -> {
                totalDonationsDtos.add(new TotalDonationsDto(
                        groupType,
                        events.stream().mapToLong(EventDto::getTotalDonations).sum(),
                        events
                ));
            }
            case "byCities" -> {
                Map<String, Long> donationsByCity = events.stream()
                        .collect(Collectors.groupingBy(EventDto::getCity, Collectors.summingLong(EventDto::getTotalDonations)));

                Map<String, List<EventDto>> eventsByCity = events.stream()
                        .collect(Collectors.groupingBy(EventDto::getCity));

                for(String zipCode: donationsByCity.keySet()) {
                    totalDonationsDtos.add(new TotalDonationsDto(zipCode, donationsByCity.get(zipCode), eventsByCity.get(zipCode)));
                }
            }
            default -> {
                boolean isCity = cities.stream().map(CityDto::getCityName).toList().contains(groupType);
                if(!isCity)
                    throw new InvalidGroupTypeException(groupType + " is not in available cities!");

                List<EventDto> filteredEvents = new ArrayList<>();
                totalEventsDonations = events.stream()
                        .filter(e -> e.getCity().equals(groupType))
                        .peek(filteredEvents::add)
                        .mapToLong(EventDto::getTotalDonations)
                        .sum();

                totalDonationsDtos.add(new TotalDonationsDto(groupType, totalEventsDonations, filteredEvents));
            }
        }

        return ResponseEntity.ok(totalDonationsDtos);
    }
}
