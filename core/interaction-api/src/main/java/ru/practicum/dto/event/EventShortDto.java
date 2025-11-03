package ru.practicum.dto.event;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    Long id;
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    String eventDate;
    Long initiator;
    Boolean paid;
    String title;
    Integer views;
}