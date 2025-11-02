package ru.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.RequestState;

import java.util.List;

@FeignClient(value = "request-service", path = "/users")
public interface RequestClient {

    @GetMapping("/{user-id}/events/{event-id}/requests")
    List<ParticipationRequestDto> getRequestsOfUser(@PathVariable(name = "user-id") long userId,
                                                    @PathVariable(name = "event-id") long eventId);

    @GetMapping("/requests/{request-id}")
    ParticipationRequestDto getRequestById(@PathVariable(name = "request-id")long requestId);

    @PostMapping("/requests/{request-id}/status")
    ParticipationRequestDto updateRequestStatus(@PathVariable(name = "request-id") long requestId,
                                                @RequestParam RequestState requestState);
}
