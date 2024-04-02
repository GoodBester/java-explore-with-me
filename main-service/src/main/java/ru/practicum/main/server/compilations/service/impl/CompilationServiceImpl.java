package ru.practicum.main.server.compilations.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.main.server.compilations.dto.CompilationDto;
import ru.practicum.main.server.compilations.dto.NewCompilationDto;
import ru.practicum.main.server.compilations.model.Compilation;
import ru.practicum.main.server.compilations.repository.CompilationRepository;
import ru.practicum.main.server.compilations.service.CompilationService;
import ru.practicum.main.server.error.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final ModelMapper mapper;
    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        return compilationRepository.findAllByPinned(pinned, PageRequest.of(from, size));
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        return mapper.map(compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(HttpStatus.NOT_FOUND, "Подборка не найдена или недоступна")), CompilationDto.class);
    }

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = mapper.map(newCompilationDto, Compilation.class);
        return mapper.map(compilationRepository.save(compilation), CompilationDto.class);
    }

    @Override
    public String deleteCompilation(Long compId) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND ,"Подборка не найдена или недоступна"));
        compilationRepository.deleteById(compId);
        return "Подборка удалена";
    }

    @Override
    public CompilationDto updateCompilation(Long compId) {

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Подборка не найдена или недоступна"));

        Compilation updated = compilationRepository.save(compilation);
        return mapper.map(updated, CompilationDto.class);
    }
}
