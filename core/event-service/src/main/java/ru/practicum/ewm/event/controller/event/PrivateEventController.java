package ru.practicum.ewm.event.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.dto.event.EventDto;
import ru.practicum.api.dto.event.NewEventDto;
import ru.practicum.api.dto.event.UpdateEventUserRequest;
import ru.practicum.api.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.api.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.api.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.event.param.PrivateEventParam;
import ru.practicum.ewm.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class PrivateEventController {

    private final EventService eventService;
    private final String eventsPath = "/{user-id}/events";
    private final String eventPath = "/{user-id}/events/{event-id}";
    private final String requestsPath = "/{user-id}/events/{event-id}/requests";

    @GetMapping(eventsPath)
    public List<EventDto> getEventsOfUser(@PathVariable(name = "user-id") long userId,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        PrivateEventParam param = PrivateEventParam.builder()
                .userId(userId)
                .from(from)
                .size(size)
                .build();
        return eventService.getEventsOfUser(param);
    }

    @GetMapping(eventPath)
    public EventDto getEventOfUser(@PathVariable(name = "user-id") long userId,
                                   @PathVariable(name = "event-id") long eventId) {
        PrivateEventParam param = PrivateEventParam.builder()
                .userId(userId)
                .eventId(eventId)
                .build();
        return eventService.getEventOfUser(param);
    }

    @PostMapping(eventsPath)
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(@Valid @RequestBody NewEventDto event,
                                @PathVariable(name = "user-id") long userId) {
        log.debug("получаем запрос на создание события");
        PrivateEventParam param = PrivateEventParam.builder()
                .newEvent(event)
                .userId(userId)
                .build();
        return eventService.createEvent(param);
    }

    @PatchMapping(eventPath)
    public EventDto updateEvent(@Valid @RequestBody UpdateEventUserRequest event,
                                @PathVariable(name = "user-id") long userId,
                                @PathVariable(name = "event-id") long eventId) {
        PrivateEventParam param = PrivateEventParam.builder()
                .eventOnUpdate(event)
                .userId(userId)
                .eventId(eventId)
                .build();
        return eventService.updateEvent(param);
    }

    @GetMapping(requestsPath)
    public List<ParticipationRequestDto> getRequestsOfUser(@PathVariable(name = "user-id") long userId,
                                                           @PathVariable(name = "event-id") long eventId) {
        PrivateEventParam param = PrivateEventParam.builder()
                .userId(userId)
                .eventId(eventId)
                .build();
        return eventService.getRequestsOfUser(param);
    }

    @PatchMapping(requestsPath)
    public EventRequestStatusUpdateResult updateStatusOfRequests(@RequestBody EventRequestStatusUpdateRequest request,
                                                                 @PathVariable(name = "user-id") long userId,
                                                                 @PathVariable(name = "event-id") long eventId) {
        PrivateEventParam param = PrivateEventParam.builder()
                .request(request)
                .userId(userId)
                .eventId(eventId)
                .build();
        return eventService.updateStatusOfRequests(param);
    }
}