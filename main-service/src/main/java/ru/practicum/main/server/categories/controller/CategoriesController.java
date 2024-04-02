package ru.practicum.main.server.categories.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.server.categories.dto.CategoryDto;
import ru.practicum.main.server.categories.dto.NewCategoryDto;
import ru.practicum.main.server.categories.service.CategoriesService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CategoriesController {

    private final CategoriesService categoriesService;


    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(required = false, defaultValue = "0") Integer from,
                                           @RequestParam(required = false, defaultValue = "10") Integer size) {
        return categoriesService.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        return categoriesService.getCategory(catId);
    }

    @PostMapping("/admin/categories")
    public CategoryDto addCategory(@RequestBody NewCategoryDto newCategoryDto) {
        return categoriesService.addCategory(newCategoryDto);
    }

    @PostMapping("/admin/categories/{catId}")
    public String deleteCategory(@PathVariable Long catId) {
        return categoriesService.deleteCategory(catId);
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @RequestBody NewCategoryDto newCategoryDto) {
        return categoriesService.updateCategory(catId, newCategoryDto);
    }
}
