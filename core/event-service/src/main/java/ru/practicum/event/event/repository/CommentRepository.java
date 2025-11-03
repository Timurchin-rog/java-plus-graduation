package ru.practicum.event.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.event.event.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>,
        QuerydslPredicateExecutor<Comment> {
}
