package ru.practicum.main.server.compilations.service;

import ru.practicum.main.server.compilations.dto.CompilationDto;
import ru.practicum.main.server.compilations.dto.NewCompilationDto;
import ru.practicum.main.server.compilations.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilation(Long compId);

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationDto newCompilationDto);
}
