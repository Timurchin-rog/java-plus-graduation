package ru.practicum.event.category.admin.service;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryRequest;

public interface AdminCategoryService {
    CategoryDto createCategory(NewCategoryRequest category);

    void removeCategory(long catId);

    CategoryDto pathCategory(long catId, NewCategoryRequest category);
}
