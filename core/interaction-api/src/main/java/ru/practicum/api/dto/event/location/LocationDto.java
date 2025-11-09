package ru.practicum.api.dto.event.location;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationDto {
    Long id;
    Double lat;
    Double lon;
}
