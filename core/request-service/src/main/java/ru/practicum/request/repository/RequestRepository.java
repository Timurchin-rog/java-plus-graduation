package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long>, QuerydslPredicateExecutor<Request> {
    @Query("SELECT r.eventId as eventId, COUNT(r.id) as count " +
            "FROM Request r " +
            "WHERE r.eventId IN :eventsIds " +
            "AND r.state = :state " +
            "GROUP BY r.eventId")
    List<Object[]> getCountByEventIdInAndStatus(
            @Param("eventsIds") List<Long> eventsIds,
            @Param("state") String state);
}
