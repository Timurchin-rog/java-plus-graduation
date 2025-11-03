package ru.practicum.dto.event;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.location.LocationDto;
import ru.practicum.dto.user.UserShortDto;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    Long id;
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    String createdOn;
    String description;
    String eventDate;
    Long initiatorId;
    UserShortDto initiator;
    LocationDto location;
    Boolean paid;
    Long participantLimit;
    String publishedOn;
    Boolean requestModeration;
    String state;
    String title;
    Integer views;
}
