package ru.practicum.event.event.service;

import ru.practicum.event.event.param.AdminCommentParam;
import ru.practicum.event.event.param.OpenCommentParam;
import ru.practicum.event.event.param.PrivateCommentParam;
import ru.practicum.event.event.param.PrivateEventParam;
import ru.practicum.dto.event.EventFilter;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.event.comment.CommentDto;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.List;

public interface EventService {

    List<EventFullDto> getEventsOfUser(PrivateEventParam param);

    EventFullDto getEventOfUser(PrivateEventParam param);

    EventFullDto getEventById(long eventId);

    EventFullDto createEvent(PrivateEventParam param);

    void addConfirmedRequest(long eventId);

    void removeConfirmedRequest(long eventId);

    EventFullDto updateEvent(PrivateEventParam param);

    List<ParticipationRequestDto> getRequestsOfUser(PrivateEventParam param);

    EventRequestStatusUpdateResult updateStatusOfRequests(PrivateEventParam param);

    Collection<EventShortDto> getPublicAllEvents(EventFilter filter, HttpServletRequest request);

    EventFullDto getPublicEvent(Long eventId, HttpServletRequest request);

    Collection<EventFullDto> getAdminAllEvents(EventFilter filter);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEvent);

    List<CommentDto> getCommentsOfUser(PrivateCommentParam param);

    CommentDto createComment(PrivateCommentParam param);

    List<CommentDto> getComments(OpenCommentParam param);

    CommentDto getCommentById(AdminCommentParam param);

    CommentDto updateComment(AdminCommentParam param);

    void removeComment(AdminCommentParam param);
}
