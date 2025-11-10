package ru.practicum.ewm.event.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.dto.event.EventDto;
import ru.practicum.api.feign.EventOperations;
import ru.practicum.ewm.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
public class ApiEventController implements EventOperations {
    private final EventService eventService;
    private final String userHeader = "X-EWM-USER-ID";

    @Override
    public EventDto getEventApi(@PathVariable(name = "event-id") Long eventId) {
        return eventService.getEventApi(eventId);
    }

    @Override
    public void addConfirmedRequest(@PathVariable(name = "event-id") Long eventId) {
        eventService.addConfirmedRequest(eventId);
    }

    @Override
    public void removeConfirmedRequest(@PathVariable(name = "event-id") Long eventId) {
        eventService.addConfirmedRequest(eventId);
    }

    @Override
    public void likeEvent(@RequestHeader(userHeader) Long userId, @PathVariable Long eventId) {
        log.info("Запрос на лайк для события с id: {} от пользователя с id: {}", eventId, userId);
        eventService.likeEvent(userId, eventId);
    }

    @Override
    public List<EventDto> getRecommendations(@RequestHeader(userHeader) Long userId) {
        log.info("Запрос на получение рекомендаций для пользователя с id: {}", userId);
        return eventService.getRecommendations(userId);
    }
}
