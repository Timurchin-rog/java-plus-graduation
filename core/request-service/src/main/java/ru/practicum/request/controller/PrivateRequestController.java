package ru.practicum.request.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.RequestState;
import ru.practicum.request.param.PrivateRequestParam;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class PrivateRequestController {
    private final String requestsEventPath = "/{user-id}/events/{event-id}/requests";
    private final String requestsPath = "/{user-id}/requests";
    private final RequestService requestService;

    @GetMapping(requestsPath)
    public List<ParticipationRequestDto> getRequestsOfCurrentUser(@Positive @PathVariable(name = "user-id") long userId) {
        PrivateRequestParam param = PrivateRequestParam.builder()
                .userId(userId)
                .build();
        return requestService.getRequestOfCurrentUser(param);
    }

    @GetMapping(requestsEventPath)
    public List<ParticipationRequestDto> getRequestsOfUser(@PathVariable(name = "user-id") long userId,
                                                           @PathVariable(name = "event-id") long eventId) {
        PrivateRequestParam param = PrivateRequestParam.builder()
                .userId(userId)
                .eventId(eventId)
                .build();
        return requestService.getRequestsOfUser(param);
    }

    @GetMapping("/requests/{request-id}")
    public ParticipationRequestDto getRequestById(@PathVariable(name = "request-id") long requestId) {
        return requestService.getRequestById(requestId);
    }

    @PostMapping("/requests/{request-id}/status")
    public void updateRequestStatus(@PathVariable(name = "request-id") long requestId,
                                    @RequestParam RequestState requestState) {
        PrivateRequestParam param = PrivateRequestParam.builder()
                .requestId(requestId)
                .requestState(requestState)
                .build();
        requestService.updateRequestStatus(param);
    }

    @PostMapping(requestsPath)
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable(name = "user-id") long userId,
                                                 @RequestParam long eventId) {
        PrivateRequestParam param = PrivateRequestParam.builder()
                .userId(userId)
                .eventId(eventId)
                .build();
        return requestService.createRequest(param);
    }

    @PatchMapping("/{user-id}/requests/{request-id}/cancel")
    public ParticipationRequestDto updateRequest(@PathVariable(name = "user-id") long userId,
                                                 @PathVariable(name = "request-id") long requestId) {
        PrivateRequestParam param = PrivateRequestParam.builder()
                .userId(userId)
                .requestId(requestId)
                .build();
        return requestService.updateRequest(param);
    }
}