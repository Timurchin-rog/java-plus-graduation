package ru.practicum.ewm.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByTitleLike(String title);

    List<Category> getByIdIn(List<Long> ids);
}
