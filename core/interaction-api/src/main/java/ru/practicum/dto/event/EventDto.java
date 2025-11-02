package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.location.LocationDto;
import ru.practicum.dto.user.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDto {
    Long id;

    String annotation;

    Long categoryId;

    CategoryDto category;

    Long confirmedRequests;

    LocalDateTime createdOn;

    LocalDateTime publishedOn;

    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    UserDto initiator;

    LocationDto location;

    Boolean paid;

    Long participantLimit;

    Boolean requestModeration;

    String state;

    String title;

    Long views;
}
