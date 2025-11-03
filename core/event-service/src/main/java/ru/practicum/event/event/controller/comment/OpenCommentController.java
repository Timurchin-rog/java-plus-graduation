package ru.practicum.event.event.controller.comment;

import ru.practicum.event.event.param.OpenCommentParam;
import ru.practicum.event.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.comment.CommentDto;

import java.util.Collection;

@RestController
@RequestMapping(path = "/events/{event-id}/comments")
@RequiredArgsConstructor
public class OpenCommentController {
    private final EventService eventService;

    @GetMapping
    public Collection<CommentDto> getComments(@PathVariable(name = "event-id") long eventId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        OpenCommentParam param = OpenCommentParam.builder()
                .eventId(eventId)
                .from(from)
                .size(size)
                .build();
        return eventService.getComments(param);
    }
}
