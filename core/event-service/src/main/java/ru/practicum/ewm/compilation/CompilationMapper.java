package ru.practicum.ewm.compilation;

import ru.practicum.api.dto.compilation.CompilationDto;
import ru.practicum.api.dto.compilation.NewCompilationDto;
import ru.practicum.api.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.api.exception.ValidationException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CompilationMapper {

    public static CompilationDto mapToCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(EventMapper.mapToEventDto(compilation.getEvents()))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static List<CompilationDto> mapToCompilationDto(Iterable<Compilation> compilations) {
        List<CompilationDto> compilationsResult = new ArrayList<>();

        for (Compilation compilation : compilations) {
            compilationsResult.add(mapToCompilationDto(compilation));
        }

        return compilationsResult.stream()
                .sorted(Comparator.comparing(CompilationDto::getId))
                .toList();
    }

    public static Compilation mapFromRequest(NewCompilationDto compilation, List<Event> events) {
        return new Compilation(
                events,
                validatePinned(compilation.getPinned()),
                validateTitle(compilation.getTitle())
        );
    }

    private static Boolean validatePinned(Boolean pinned) {
        if (pinned == null) {
            pinned = false;
        }

        return pinned;
    }

    private static String validateTitle(String title) {
        if (title.length() > 50) {
            throw new ValidationException("Название подборки не может быть больше 50 символов");
        } else {
            return title;
        }
    }

    public static Compilation updateCompilationFields(Compilation compilation, UpdateCompilationRequest compilationFromRequest) {
        if (compilationFromRequest.hasPinned()) {
            compilation.setPinned(compilationFromRequest.getPinned());
        }

        if (compilationFromRequest.hasTitle()) {
            compilation.setTitle(validateTitle(compilationFromRequest.getTitle()));
        }

        return compilation;
    }
}