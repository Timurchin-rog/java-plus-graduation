package ru.practicum.ewm.event.service;

import ru.practicum.api.dto.event.EventDto;
import ru.practicum.ewm.event.param.AdminCommentParam;
import ru.practicum.ewm.event.param.OpenCommentParam;
import ru.practicum.ewm.event.param.PrivateCommentParam;
import ru.practicum.ewm.event.param.PrivateEventParam;
import ru.practicum.api.dto.event.EventFilter;
import ru.practicum.api.dto.event.UpdateEventAdminRequest;
import ru.practicum.api.dto.event.comment.CommentDto;
import ru.practicum.api.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.api.dto.request.ParticipationRequestDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.List;

public interface EventService {

    List<EventDto> getEventsOfUser(PrivateEventParam param);

    EventDto getEventOfUser(PrivateEventParam param);

    EventDto getEventById(Long eventId);

    EventDto getEventApi(Long eventId);

    List<EventDto> getRecommendations(Long userId);

    EventDto createEvent(PrivateEventParam param);

    void addConfirmedRequest(long eventId);

    void removeConfirmedRequest(long eventId);

    EventDto updateEvent(PrivateEventParam param);

    List<ParticipationRequestDto> getRequestsOfUser(PrivateEventParam param);

    EventRequestStatusUpdateResult updateStatusOfRequests(PrivateEventParam param);

    Collection<EventDto> getPublicAllEvents(EventFilter filter, HttpServletRequest request);

    EventDto getPublicEvent(Long userId, Long eventId, HttpServletRequest request);

    Collection<EventDto> getAdminAllEvents(EventFilter filter);

    EventDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEvent);

    List<CommentDto> getCommentsOfUser(PrivateCommentParam param);

    CommentDto createComment(PrivateCommentParam param);

    List<CommentDto> getComments(OpenCommentParam param);

    CommentDto getCommentById(AdminCommentParam param);

    CommentDto updateComment(AdminCommentParam param);

    void removeComment(AdminCommentParam param);

    void likeEvent(Long userId, Long eventId);
}
