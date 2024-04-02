package ru.practicum.main.server.compilations.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.main.server.compilations.dto.CompilationDto;
import ru.practicum.main.server.compilations.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilation(Long compId);

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    String deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId);
}
