package ru.practicum.ewm.event.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Optional<Event> findByIdAndInitiatorId(long eventId, long userId);

    boolean existsByCategoryId(long categoryId);

    @Modifying
    @Query("UPDATE Event e SET e.confirmedRequests = :count WHERE e.id = :eventId")
    void setConfirmedRequestsCount(
            @Param("eventId") Long eventId,
            @Param("count") Long count);

    Set<Event> findAllByIdIn(Set<Long> ids);
}
