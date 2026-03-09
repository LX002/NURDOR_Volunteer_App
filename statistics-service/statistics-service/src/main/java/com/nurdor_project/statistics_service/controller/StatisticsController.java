package com.nurdor_project.statistics_service.controller;

import com.nurdor_project.statistics_service.dto.*;
import com.nurdor_project.statistics_service.exception.InvalidGroupTypeException;
import com.nurdor_project.statistics_service.proxy.DonationsProxy;
import com.nurdor_project.statistics_service.proxy.EventProxy;
import com.nurdor_project.statistics_service.proxy.VolunteerProxy;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/statistics")
public class StatisticsController {

    /* TODO add following stuff here
    * [DONE] ukupne donacije za sve zavrsene eventove (uzima se danasnji datetime kao gornja granica zavrsetka dogadjaja) (event-service, dodaj totalDonations kolonu u event)
    * [DONE] volonteri po (najblizim) gradovima (volunteer-service) (broj volontera?)
    * [DONE] broj prijavljenih volontera za odredjeni dogadjaj + njihov spisak (events-log)
    * [DONE] broj / lista prisutnih volontera za dogadjaje koji su u toku (events-log)
    * broj tekucih dogadjaja, njihovo izlistavanje zajedno sa standovima? (koristi HATEOAS)
    * mozda da iskoristis hateoas na ostalim?
    * */

    private VolunteerProxy volunteerProxy;
    private EventProxy eventProxy;
    private DonationsProxy donationsProxy;

    @RateLimiter(name = "calculateTotalDonations", fallbackMethod = "fallback")
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
                boolean isCity = cities.stream().map(CityDto::getZipCode).toList().contains(groupType);
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

    @RateLimiter(name = "volunteersByCities", fallbackMethod = "fallback")
    @GetMapping("/count/volunteersByCities")
    public ResponseEntity<Map<String, Long>> countVolunteersByCities() {
        System.out.println(volunteerProxy.findAll());
        return ResponseEntity.ok(volunteerProxy.findAll().stream()
                .collect(Collectors.groupingBy(VolunteerDto::getNearestCity, Collectors.counting())));
    }

    @RateLimiter(name = "volunteersOnEvent", fallbackMethod = "fallback")
    @GetMapping("/count/volunteers")
    public ResponseEntity<VolunteersEventDto> countAndFindVolunteersOnEvent(@RequestParam("idEvent") @Min(1) Integer idEvent) {
        EventDto event = eventProxy.findById(idEvent);
        List<VolunteerDto> volunteers = volunteerProxy.findByIdEvent(idEvent);
        return ResponseEntity.ok(new VolunteersEventDto(event.getEventName(), volunteers.size(), volunteers));
    }


    @RateLimiter(name = "presentVolunteers", fallbackMethod = "fallback")
    @GetMapping("/count/presentVolunteers")
    public EntityModel<Map<String, PresentVolunteerEventDto>> countPresentVolunteersByEvent() {
        Map<String, PresentVolunteerEventDto> result = volunteerProxy.groupPresentVolunteersByEvent()
                .entrySet().stream()
                .collect(Collectors.toMap(
                        e -> "EVENT_ID: " + e.getKey(),
                        Map.Entry::getValue
                ));

        EntityModel<Map<String, PresentVolunteerEventDto>> em = EntityModel.of(result);
        for(String idEvent: em.getContent().keySet()) {
            em.add(Link.of("http://localhost:8765/volunteer/event/" + idEvent).withRel("event" + idEvent + "-details"));
        }
        em.add(Link.of("http://localhost:8765/admin/startedEvents").withRel("started-events-with-stands"));
        return em;
    }

    @RateLimiter(name = "startedEvents", fallbackMethod = "fallback")
    @GetMapping("/count/startedEvents")
    public ResponseEntity<EventCountDto> countStartedEvents() {
        List<DetailedEventDto> detailedEventDtos =
                eventProxy.findStartedEvents().stream()
                .map(e -> {
                    DetailedEventDto detailedEventDto = new DetailedEventDto();
                    detailedEventDto.setEvent(e);
                    detailedEventDto.setStands(donationsProxy.findByIdEvent(e.getId()));
                    return detailedEventDto;
                }).toList();
        return ResponseEntity.ok(new EventCountDto(detailedEventDtos.size(), detailedEventDtos));
    }

    public ResponseEntity<?> fallback(Exception e) {
        return new ResponseEntity<>("Too many requests!\n" + e.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
    }
}
