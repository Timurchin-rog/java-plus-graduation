package ru.practicum.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.dto.request.ParticipationRequestDto;
import ru.practicum.api.enums.RequestState;

import java.util.List;
import java.util.Map;

@FeignClient("request-service")
public interface RequestOperations {

    String apiRequestsPath = "/api/requests";

    @GetMapping("api/users/{user-id}/events/{event-id}/requests")
    List<ParticipationRequestDto> getRequestsOfUser(@PathVariable(name = "user-id") long userId,
                                                    @PathVariable(name = "event-id") long eventId);

    @GetMapping(apiRequestsPath + "/{request-id}")
    ParticipationRequestDto getRequestById(@PathVariable(name = "request-id") long requestId);

    @PostMapping(apiRequestsPath + "/{request-id}/update-status")
    void updateRequestStatus(@PathVariable(name = "request-id") long requestId,
                             @RequestParam RequestState requestState);

    @PostMapping(apiRequestsPath + "/confirmed")
    Map<Long, Long> getConfirmedEventsRequestsCount(@RequestBody List<Long> eventsIds);
}
