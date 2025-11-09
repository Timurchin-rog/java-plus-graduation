package ru.practicum.request.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.dto.request.ParticipationRequestDto;
import ru.practicum.request.param.PrivateRequestParam;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class PrivateRequestController {
    private final String requestsPath = "/{user-id}/requests";
    private final RequestService requestService;

    @GetMapping(requestsPath)
    public List<ParticipationRequestDto> getRequestsOfCurrentUser(@Positive @PathVariable(name = "user-id") long userId) {
        PrivateRequestParam param = PrivateRequestParam.builder()
                .userId(userId)
                .build();
        return requestService.getRequestOfCurrentUser(param);
    }

    @PostMapping(requestsPath)
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable(name = "user-id") Long userId,
                                                 @RequestParam Long eventId) {
        log.error("В контроллер поступил запрос на создание заявки");
        PrivateRequestParam param = PrivateRequestParam.builder()
                .userId(userId)
                .eventId(eventId)
                .build();
        return requestService.createRequest(param);
    }

    @PatchMapping(requestsPath + "/{request-id}/cancel")
    public ParticipationRequestDto updateRequest(@PathVariable(name = "user-id") long userId,
                                                 @PathVariable(name = "request-id") long requestId) {
        PrivateRequestParam param = PrivateRequestParam.builder()
                .userId(userId)
                .requestId(requestId)
                .build();
        return requestService.updateRequest(param);
    }

}