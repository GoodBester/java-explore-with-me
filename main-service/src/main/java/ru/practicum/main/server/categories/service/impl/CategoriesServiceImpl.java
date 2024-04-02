package ru.practicum.main.server.categories.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.w3c.dom.events.EventException;
import ru.practicum.main.server.categories.dto.CategoryDto;
import ru.practicum.main.server.categories.dto.NewCategoryDto;
import ru.practicum.main.server.categories.model.Category;
import ru.practicum.main.server.categories.repository.CategoryRepository;
import ru.practicum.main.server.categories.service.CategoriesService;
import ru.practicum.main.server.error.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CategoriesServiceImpl implements CategoriesService {
    private final ModelMapper mapper;
    private final CategoryRepository categoryRepository;


    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable page = PageRequest.of(from, size);
        return categoryRepository.findAll(page).stream().map(category -> mapper.map(category, CategoryDto.class)).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        return mapper.map(categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Booking not found.")), CategoryDto.class);
    }

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        return mapper.map(categoryRepository.save(mapper.map(newCategoryDto, Category.class)), CategoryDto.class);
    }

    @Override
    public String deleteCategory(Long catId) {
        categoryRepository.deleteById(catId);
        return "Категория удалена";
    }

    @Override
    public CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDto) {
        Optional<Category> category = categoryRepository.findById(catId);
        category.get().setName(newCategoryDto.getName());
        categoryRepository.save(category.get());
        return mapper.map(category.get(), CategoryDto.class);

    }
}
