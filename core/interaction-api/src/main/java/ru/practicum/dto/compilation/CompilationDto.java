package ru.practicum.dto.compilation;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.event.EventShortDto;

import java.util.List;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
    Long id;
    List<EventShortDto> events;
    Boolean pinned;
    String title;
}
