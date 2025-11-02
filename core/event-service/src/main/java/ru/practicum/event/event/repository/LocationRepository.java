package ru.practicum.event.event.repository;

import ru.practicum.event.event.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
