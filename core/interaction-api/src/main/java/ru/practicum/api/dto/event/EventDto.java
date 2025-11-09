package ru.practicum.api.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.api.dto.category.CategoryDto;
import ru.practicum.api.dto.event.location.LocationDto;
import ru.practicum.api.dto.user.UserDto;

@Getter
@Setter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDto {
    Long id;
    String annotation;
    Long categoryId;
    CategoryDto category;
    Long confirmedRequests;
    String createdOn;
    String publishedOn;
    String description;
    String eventDate;
    Long initiatorId;
    UserDto initiator;
    LocationDto location;
    Boolean paid;
    Long participantLimit;
    Boolean requestModeration;
    String state;
    String title;
    Double rating;
}
