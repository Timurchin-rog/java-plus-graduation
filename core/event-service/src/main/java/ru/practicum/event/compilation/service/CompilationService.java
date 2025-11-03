package ru.practicum.event.compilation.service;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.event.compilation.param.AdminCompilationParam;
import ru.practicum.event.compilation.param.PublicCompilationParam;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getCompilations(PublicCompilationParam param);

    CompilationDto getCompilationById(PublicCompilationParam param);

    CompilationDto createCompilation(AdminCompilationParam param);

    CompilationDto updateCompilation(AdminCompilationParam param);

    void removeCompilation(AdminCompilationParam param);
}
