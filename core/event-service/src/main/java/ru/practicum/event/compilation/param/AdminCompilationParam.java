package ru.practicum.event.compilation.param;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminCompilationParam {
    long compId;
    NewCompilationDto compilationFromRequest;
    UpdateCompilationRequest compilationOnUpdate;
}
