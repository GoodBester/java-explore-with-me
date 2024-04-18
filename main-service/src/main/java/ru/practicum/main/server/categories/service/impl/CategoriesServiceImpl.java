package ru.practicum.main.server.categories.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.main.server.categories.dto.CategoryDto;
import ru.practicum.main.server.categories.dto.NewCategoryDto;
import ru.practicum.main.server.categories.model.Category;
import ru.practicum.main.server.categories.repository.CategoryRepository;
import ru.practicum.main.server.categories.service.CategoriesService;
import ru.practicum.main.server.error.exception.IncorrectValueException;
import ru.practicum.main.server.error.exception.NotFoundException;
import ru.practicum.main.server.events.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CategoriesServiceImpl implements CategoriesService {
    private final ModelMapper mapper;
    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;


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
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new IncorrectValueException(HttpStatus.CONFLICT, "Категория с именем " + newCategoryDto.getName() + " уже существует.");
        }
        return mapper.map(categoryRepository.save(mapper.map(newCategoryDto, Category.class)), CategoryDto.class);
    }

    @Override
    public void deleteCategory(Long catId) {
        if (eventRepository.existsByCategory_Id(catId)) {
            throw new IncorrectValueException(HttpStatus.CONFLICT, "Ошибка. Категория содержит события.");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDto) {

        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Категория не найдена."));

        if (!category.getName().equals(newCategoryDto.getName()) && categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new IncorrectValueException(HttpStatus.CONFLICT, "Категория с именем " + newCategoryDto.getName() + " уже существует.");
        }
        category.setName(newCategoryDto.getName());
        return mapper.map(categoryRepository.save(category), CategoryDto.class);

    }
}
