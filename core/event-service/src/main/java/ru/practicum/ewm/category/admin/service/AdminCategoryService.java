package ru.practicum.ewm.category.admin.service;

import ru.practicum.api.dto.category.CategoryDto;
import ru.practicum.api.dto.category.NewCategoryRequest;

public interface AdminCategoryService {
    CategoryDto createCategory(NewCategoryRequest category);

    void removeCategory(long catId);

    CategoryDto pathCategory(long catId, NewCategoryRequest category);
}
