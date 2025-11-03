package ru.practicum.event.event.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFilter;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.event.event.service.EventService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@Slf4j
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService eventService;
    private final String eventPath = "/{event-id}";

    @GetMapping
    public Collection<EventFullDto> getAdminAllEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Future LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        EventFilter eventFilter = EventFilter.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        return eventService.getAdminAllEvents(eventFilter);
    }

    @GetMapping(eventPath)
    public EventFullDto getEventById(@PathVariable(name = "event-id") Long eventId) {
        return eventService.getEventById(eventId);
    }

    @GetMapping(eventPath + "/users/{user-id}")
    public EventFullDto getEventById(@PathVariable(name = "event-id") Long eventId,
                                     @PathVariable(name = "user-id") Long userId) {
        return eventService.getEventById(eventId);
    }

    @PostMapping(eventPath + "/add-request")
    public void addConfirmedRequest(@PathVariable(name = "event-id") Long eventId) {
        eventService.addConfirmedRequest(eventId);
    }

    @PostMapping(eventPath + "/remove-request")
    public void removeConfirmedRequest(@PathVariable(name = "event-id") Long eventId) {
        eventService.addConfirmedRequest(eventId);
    }

    @PatchMapping(eventPath)
    public EventFullDto updateByAdmin(@PathVariable(name = "event-id") Long eventId,
                                      @Valid @RequestBody UpdateEventAdminRequest updateEvent) {
        return eventService.updateByAdmin(eventId, updateEvent);
    }
}
