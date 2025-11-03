package ru.practicum.event.category.open.service;

import ru.practicum.dto.category.CategoryDto;

import java.util.Collection;

public interface OpenCategoryService {
    Collection<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(long catId);
}
