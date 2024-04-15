package ru.practicum.main.server.compilations.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.server.compilations.dto.CompilationDto;
import ru.practicum.main.server.compilations.dto.NewCompilationDto;
import ru.practicum.main.server.compilations.dto.UpdateCompilationDto;
import ru.practicum.main.server.compilations.model.Compilation;
import ru.practicum.main.server.compilations.repository.CompilationRepository;
import ru.practicum.main.server.compilations.service.CompilationService;
import ru.practicum.main.server.error.exception.NotFoundException;
import ru.practicum.main.server.events.model.Event;
import ru.practicum.main.server.events.repository.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final ModelMapper mapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        return compilationRepository.findAllByPinned(pinned,
                        PageRequest.of(from / size, size, Sort.by("id"))).stream().
                map(compilation -> mapper.map(compilation, CompilationDto.class)).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        return mapper.map(compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(HttpStatus.NOT_FOUND, "Подборка не найдена или недоступна")), CompilationDto.class);
    }

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = mapper.map(newCompilationDto, Compilation.class);
        compilation.setPinned(newCompilationDto.getPinned() != null && newCompilationDto.getPinned());


        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            Set<Event> events = new HashSet<>(eventRepository.findAllByIdIn(newCompilationDto.getEvents()));
            compilation.setEvents(events);
        } else {
            compilation.setEvents(new HashSet<>());
        }
        return mapper.map(compilationRepository.save(compilation), CompilationDto.class);
    }

    @Override
    public String deleteCompilation(Long compId) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Подборка не найдена или недоступна"));
        compilationRepository.deleteById(compId);
        return "Подборка удалена";
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto compilationDto) {

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Подборка не найдена или недоступна"));

        if (compilationDto.getEvents() != null && !compilationDto.getEvents().isEmpty()) {
            Set<Event> events = new HashSet<>(eventRepository.findAllByIdIn(compilationDto.getEvents()));
            compilation.setEvents(events);
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        return mapper.map(compilationRepository.save(compilation), CompilationDto.class);
    }
}
