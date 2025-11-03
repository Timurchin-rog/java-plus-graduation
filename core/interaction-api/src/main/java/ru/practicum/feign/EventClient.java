package ru.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.practicum.dto.event.EventFullDto;

@FeignClient(value = "event-service", path = "/admin/events")
public interface EventClient {

    @GetMapping("/{event-id}")
    EventFullDto getEventById(@PathVariable(name = "event-id") Long eventId);

    @PostMapping("/{event-id}/add-request")
    void addConfirmedRequest(@PathVariable(name = "event-id") Long eventId);

    @PostMapping("/{event-id}/remove-request")
    void removeConfirmedRequest(@PathVariable(name = "event-id") Long eventId);
}
