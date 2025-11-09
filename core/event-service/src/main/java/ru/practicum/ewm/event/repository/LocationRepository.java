package ru.practicum.ewm.event.repository;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Set<Event> findAllByIdIn(Set<Long> ids);
}
