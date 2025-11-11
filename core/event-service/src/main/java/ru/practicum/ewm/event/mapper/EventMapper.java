package ru.practicum.ewm.event.mapper;

import ru.practicum.api.dto.event.*;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.api.dto.event.location.LocationDto;
import ru.practicum.api.dto.event.location.NewLocationDto;
import ru.practicum.api.enums.EventState;
import ru.practicum.api.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventMapper {

    private static String datePattern = "yyyy-MM-dd HH:mm:ss";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

    public static EventDto mapToEventDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .categoryId(event.getCategory().getId())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(formatter))
                .publishedOn(event.getPublishedOn().format(formatter))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(formatter))
                .initiatorId(event.getInitiatorId())
                .location(mapToLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState().toString())
                .title(event.getTitle())
                .build();
    }

    public static List<EventDto> mapToEventDto(Iterable<Event> events) {
        List<EventDto> eventsResult = new ArrayList<>();

        for (Event event : events) {
            eventsResult.add(mapToEventDto(event));
        }

        return eventsResult;
    }

    public static Location mapFromRequest(NewLocationDto location) {
        return new Location(
                location.getLat(),
                location.getLon()
        );
    }

    public static LocationDto mapToLocationDto(Location location) {
        return LocationDto.builder()
                .id(location.getId())
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public static List<LocationDto> mapToLocationDto(Iterable<Location> locations) {
        List<LocationDto> locationsResult = new ArrayList<>();

        for (Location location : locations) {
            locationsResult.add(mapToLocationDto(location));
        }

        return locationsResult;
    }

    public static Event mapFromRequest(NewEventDto event) {
        return new Event(
                event.getAnnotation(),
                event.getDescription(),
                LocalDateTime.parse(event.getEventDate(), formatter),
                mapFromRequest(event.getLocation()),
                validatePaid(event.getPaid()),
                validateParticipantLimit(event.getParticipantLimit()),
                validateRequestModeration(event.getRequestModeration()),
                event.getTitle()
        );
    }

    private static Boolean validatePaid(Boolean paid) {
        if (paid == null) {
            paid = false;
        }

        return paid;
    }

    private static Long validateParticipantLimit(Long participantLimit) {
        if (participantLimit == null) {
            participantLimit = 0L;
        }

        return participantLimit;
    }

    private static Boolean validateRequestModeration(Boolean requestModeration) {
        if (requestModeration == null) {
            requestModeration = true;
        }

        return requestModeration;
    }

    public static Event updatePrivateEventFields(Event event, UpdateEventUserRequest eventFromRequest) {
        if (eventFromRequest.hasAnnotation()) {
            event.setAnnotation(eventFromRequest.getAnnotation());
        }

        if (eventFromRequest.hasDescription()) {
            event.setDescription(eventFromRequest.getDescription());
        }

        if (eventFromRequest.hasEventDate()) {
            event.setEventDate(LocalDateTime.parse(eventFromRequest.getEventDate(), formatter));
        }

        if (eventFromRequest.hasPaid()) {
            event.setPaid(eventFromRequest.getPaid());
        }

        if (eventFromRequest.hasParticipantLimit()) {
            if (eventFromRequest.getParticipantLimit() < 0)
                throw new ValidationException("Лимит участников не может быть отрицательным");
            event.setParticipantLimit(eventFromRequest.getParticipantLimit());
        }

        if (eventFromRequest.hasRequestModeration()) {
            event.setRequestModeration(eventFromRequest.getRequestModeration());
        }

        if (eventFromRequest.hasStateAction()) {
            if (eventFromRequest.getStateAction().equalsIgnoreCase("SEND_TO_REVIEW")
            && event.getState().equals(EventState.CANCELED))
                event.setState(EventState.PENDING);
            else if (eventFromRequest.getStateAction().equalsIgnoreCase("CANCEL_REVIEW"))
                event.setState(EventState.CANCELED);
        }

        if (eventFromRequest.hasTitle()) {
            event.setTitle(eventFromRequest.getTitle());
        }

        return event;
    }

    public static Event updateAdminEventFields(Event event, UpdateEventAdminRequest eventFromRequest) {
        if (eventFromRequest.hasAnnotation()) {
            event.setAnnotation(eventFromRequest.getAnnotation());
        }

        if (eventFromRequest.hasDescription()) {
            event.setDescription(eventFromRequest.getDescription());
        }

        if (eventFromRequest.hasEventDate()) {
            event.setEventDate(eventFromRequest.getEventDate());
        }

        if (eventFromRequest.hasPaid()) {
            event.setPaid(eventFromRequest.getPaid());
        }

        if (eventFromRequest.hasParticipantLimit()) {
            if (eventFromRequest.getParticipantLimit() < 0)
                throw new ValidationException("Лимит участников не может быть отрицательным");
            event.setParticipantLimit(eventFromRequest.getParticipantLimit());
        }

        if (eventFromRequest.hasRequestModeration()) {
            event.setRequestModeration(eventFromRequest.getRequestModeration());
        }

        if (eventFromRequest.hasStateAction()) {
            if (eventFromRequest.getStateAction().equalsIgnoreCase("PUBLISH_EVENT"))
                event.setState(EventState.PUBLISHED);
            else if (eventFromRequest.getStateAction().equalsIgnoreCase("REJECT_EVENT"))
                event.setState(EventState.CANCELED);
        }

        if (eventFromRequest.hasTitle()) {
            event.setTitle(eventFromRequest.getTitle());
        }

        return event;
    }
}