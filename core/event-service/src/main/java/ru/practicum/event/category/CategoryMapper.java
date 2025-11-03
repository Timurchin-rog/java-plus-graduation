package ru.practicum.event.category;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryRequest;

public class CategoryMapper {
    public static CategoryDto mapToCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getTitle())
                .build();
    }

    public static Category mapFromRequest(NewCategoryRequest categoryRequest) {
        return new Category(
                categoryRequest.getName()
        );
    }
}
