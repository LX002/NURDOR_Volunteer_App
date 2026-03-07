package com.nurdor_project.statistics_service.proxy;

import com.nurdor_project.statistics_service.dto.EventDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "event-proxy")
public interface EventProxy {

    @GetMapping("/api/admin/events/finished")
    List<EventDto> findFinishedEvents();
}
