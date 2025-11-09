package ru.practicum.ewm.compilation.service;

import ru.practicum.api.dto.compilation.CompilationDto;
import ru.practicum.ewm.compilation.param.AdminCompilationParam;
import ru.practicum.ewm.compilation.param.PublicCompilationParam;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getCompilations(PublicCompilationParam param);

    CompilationDto getCompilationById(PublicCompilationParam param);

    CompilationDto createCompilation(AdminCompilationParam param);

    CompilationDto updateCompilation(AdminCompilationParam param);

    void removeCompilation(AdminCompilationParam param);
}
