package ru.practicum.ewm.category;

import ru.practicum.api.dto.category.CategoryDto;
import ru.practicum.api.dto.category.NewCategoryRequest;

import java.util.ArrayList;
import java.util.List;

public class CategoryMapper {
    public static CategoryDto mapToCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getTitle())
                .build();
    }

    public static List<CategoryDto> mapToCategoryDto(Iterable<Category> categories) {
        List<CategoryDto> categoryResult = new ArrayList<>();

        for (Category category : categories) {
            categoryResult.add(mapToCategoryDto(category));
        }

        return categoryResult;
    }

    public static Category mapFromRequest(NewCategoryRequest categoryRequest) {
        return new Category(
                categoryRequest.getName()
        );
    }
}
