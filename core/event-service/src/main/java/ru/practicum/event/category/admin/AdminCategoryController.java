package ru.practicum.event.category.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryRequest;
import ru.practicum.event.category.admin.service.AdminCategoryService;

@RestController
@RequestMapping(path = "/admin/categories")
@Slf4j
@RequiredArgsConstructor
public class AdminCategoryController {
    private final AdminCategoryService adminCategoryService;
    private final String pathCategory = "/{cat-id}";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryRequest category) {
        log.trace("Получаем запрос на создание категории {}", category);
        return adminCategoryService.createCategory(category);
    }

    @DeleteMapping(pathCategory)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable(name = "cat-id") long catId) {
        log.trace("Получаем запрос на удаление категории с id {}", catId);
        adminCategoryService.removeCategory(catId);
    }

    @PatchMapping(pathCategory)
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto patchCategory(@PathVariable(name = "cat-id") long catId,
                                     @Valid @RequestBody NewCategoryRequest category) {
        log.trace("Получаем запрос на изменение категории с id {}. Новое имя {}", catId, category);
        return adminCategoryService.pathCategory(catId, category);
    }
}
