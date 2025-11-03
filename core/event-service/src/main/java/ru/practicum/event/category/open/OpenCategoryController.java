package ru.practicum.event.category.open;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.event.category.open.service.OpenCategoryService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/categories")
@Slf4j
@RequiredArgsConstructor
public class OpenCategoryController {
    private final OpenCategoryService openCategoryService;

    @GetMapping
    public Collection<CategoryDto> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.trace("Возвращаем список категорий от {} список размером {}", from, size);
        return openCategoryService.getCategories(from, size);
    }

    @GetMapping("/{cat-id}")
    public CategoryDto getCategory(@PathVariable(name = "cat-id") long catId) {
        log.trace("Возвращаем категорию с id {}", catId);
        return openCategoryService.getCategory(catId);
    }
}
