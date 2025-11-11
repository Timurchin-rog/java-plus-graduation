package ru.practicum.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.dto.event.EventDto;

import java.util.List;

@FeignClient("event-service")
public interface EventOperations {

    String apiEventPath = "/api/events/{event-id}";

    @GetMapping(apiEventPath)
    EventDto getEventApi(@PathVariable(name = "event-id") Long eventId);

    @PostMapping(apiEventPath + "/add-request")
    void addConfirmedRequest(@PathVariable(name = "event-id") Long eventId);

    @PostMapping(apiEventPath + "/remove-request")
    void removeConfirmedRequest(@PathVariable(name = "event-id") Long eventId);

    @PutMapping(apiEventPath + "/like")
    void likeEvent(@RequestHeader("X-EWM-USER-ID") Long userId, @PathVariable Long eventId);

    @GetMapping("/recommendations")
    List<EventDto> getRecommendations(@RequestHeader("X-EWM-USER-ID") Long userId);
}
