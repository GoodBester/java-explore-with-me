package ru.practicum.main.server.compilations.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.server.categories.dto.CategoryDto;
import ru.practicum.main.server.categories.dto.NewCategoryDto;
import ru.practicum.main.server.compilations.dto.CompilationDto;
import ru.practicum.main.server.compilations.dto.NewCompilationDto;
import ru.practicum.main.server.compilations.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CompilationController {

    private final CompilationService compilationService;

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        return compilationService.getCompilations(pinned ,from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        return compilationService.getCompilation(compId);
    }

    @PostMapping("/admin/compilations")
    public CompilationDto addCompilation(@RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.addCompilation(newCompilationDto);
    }

    @DeleteMapping("admin/compilations/{compId}")
    public String deleteCompilation(@PathVariable Long compId) {
        return compilationService.deleteCompilation(compId);
    }

    @PatchMapping("admin/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId) {
        return compilationService.updateCompilation(compId);
    }
}
