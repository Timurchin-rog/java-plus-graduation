package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.dto.request.ParticipationRequestDto;
import ru.practicum.api.enums.RequestState;
import ru.practicum.api.feign.RequestOperations;
import ru.practicum.request.param.PrivateRequestParam;
import ru.practicum.request.service.RequestService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
public class ApiRequestController implements RequestOperations {
    private final RequestService requestService;

    @Override
    public List<ParticipationRequestDto> getRequestsOfUser(@PathVariable(name = "user-id") long userId,
                                                           @PathVariable(name = "event-id") long eventId) {
        PrivateRequestParam param = PrivateRequestParam.builder()
                .userId(userId)
                .eventId(eventId)
                .build();
        return requestService.getRequestsOfUser(param);
    }

    @Override
    public ParticipationRequestDto getRequestById(@PathVariable(name = "request-id") long requestId) {
        return requestService.getRequestById(requestId);
    }

    @Override
    public void updateRequestStatus(@PathVariable(name = "request-id") long requestId,
                                    @RequestParam RequestState requestState) {
        PrivateRequestParam param = PrivateRequestParam.builder()
                .requestId(requestId)
                .requestState(requestState)
                .build();
        requestService.updateRequestStatus(param);
    }

    @Override
    public Map<Long, Long> getConfirmedEventsRequestsCount(@RequestBody List<Long> eventsIds) {
        return requestService.getConfirmedEventsRequestsCount(eventsIds);
    }
}
