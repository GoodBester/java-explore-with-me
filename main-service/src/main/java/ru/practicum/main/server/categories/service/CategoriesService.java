package ru.practicum.main.server.categories.service;

import ru.practicum.main.server.categories.dto.CategoryDto;
import ru.practicum.main.server.categories.dto.NewCategoryDto;

import java.util.List;

public interface CategoriesService {

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategory(Long CatId);

    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    String deleteCategory(Long CatId);

    CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDto);
}
