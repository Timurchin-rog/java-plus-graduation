package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ru.practicum.api.dto.event.*;
import ru.practicum.client.AnalyzerClient;
import ru.practicum.client.CollectorClient;
import ru.practicum.api.dto.category.CategoryDto;
import ru.practicum.api.dto.event.location.LocationDto;
import ru.practicum.api.dto.user.UserDto;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.mapper.CommentMapper;
import ru.practicum.ewm.event.model.*;
import ru.practicum.ewm.event.param.AdminCommentParam;
import ru.practicum.ewm.event.param.OpenCommentParam;
import ru.practicum.ewm.event.param.PrivateCommentParam;
import ru.practicum.ewm.event.param.PrivateEventParam;
import ru.practicum.ewm.event.repository.CommentRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.LocationRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.api.dto.event.comment.CommentDto;
import ru.practicum.api.dto.event.comment.NewCommentDto;
import ru.practicum.api.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.api.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.api.dto.request.ParticipationRequestDto;
import ru.practicum.api.enums.EventState;
import ru.practicum.api.enums.RequestState;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.api.exception.ConflictException;
import ru.practicum.api.exception.NotFoundException;
import ru.practicum.api.exception.ValidationException;
import ru.practicum.api.exception.WrongTimeEventException;
import ru.practicum.api.feign.RequestOperations;
import ru.practicum.api.feign.UserOperations;
import ru.practicum.api.dto.event.comment.UpdateCommentDto;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserOperations userOperations;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestOperations requestOperations;
    private final CommentRepository commentRepository;
    private final JPAQueryFactory queryFactory;
    private final CollectorClient collectorClient;
    private final AnalyzerClient analyzerClient;

    private static String datePattern = "yyyy-MM-dd HH:mm:ss";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

    @Override
    public List<EventDto> getEventsOfUser(PrivateEventParam param) {
        QEvent qEvent = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(QEvent.event.initiatorId.eq(param.getUserId()));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(param.getFrom(), param.getSize(), sortById);

        return EventMapper.mapToEventDto(eventRepository.findAll(finalCondition, page));
    }

    @Override
    public void likeEvent(Long userId, Long eventId) {
        if (requestOperations.getRequestsOfUser(userId, eventId).isEmpty()) {
            throw new ValidationException(
                    String.format("Пользователь id = %d не посещал событие c id = %d ", userId, eventId)
            );
        }
        collectorClient.collectUserAction(userId, eventId, ActionTypeProto.ACTION_LIKE, Instant.now());
    }

    @Override
    public EventDto getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .map(this::addInfo)
                .orElseThrow(() -> new NotFoundException(String.format("Событие id = %d не найдено", eventId)));
    }

    @Override
    public EventDto getEventApi(Long eventId) {
        return EventMapper.mapToEventDto(checkEvent(eventId));
    }

    private List<EventDto> addInfo(List<Event> events) {
        List<Long> eventsIds = events.stream().map(Event::getId).toList();
        List<Long> categoryIds = events.stream().map(event -> event.getCategory().getId()).toList();
        List<Long> usersIds = events.stream().map(Event::getInitiatorId).toList();
        List<Long> locationsIds = events.stream().map(event -> event.getLocation().getId()).toList();

        List<CategoryDto> categories = CategoryMapper.mapToCategoryDto(categoryRepository.getByIdIn(categoryIds));
        List<UserDto> users;
        users = userOperations.getUsers(usersIds);
        List<LocationDto> locations = EventMapper.mapToLocationDto(locationRepository.findAllById(locationsIds));
        Map<Long, Long> requests;
        requests = requestOperations.getConfirmedEventsRequestsCount(eventsIds);
        Map<Long, CategoryDto> catsByIds = categories.stream()
                .collect(Collectors.toMap(CategoryDto::getId, Function.identity()));

        Map<Long, UserDto> usersByIds = users.stream()
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));

        Map<Long, LocationDto> locationsByIds = locations.stream()
                .collect(Collectors.toMap(LocationDto::getId, Function.identity()));

        return events.stream()
                .map(e -> {
                    EventDto x = EventMapper.mapToEventDto(e);
                    x.setConfirmedRequests(requests.getOrDefault(x.getId(), 0L));
                    x.setCategory(catsByIds.getOrDefault(x.getCategory().getId(), null));
                    x.setInitiator(usersByIds.getOrDefault(x.getInitiator().getId(), null));
                    x.setLocation(locationsByIds.getOrDefault(e.getLocation().getId(), null));
                    return x;
                })
                .toList();
    }

    public EventDto addInfo(Event event) {
        return addInfo(List.of(event)).getFirst();
    }

    @Override
    public List<EventDto> getRecommendations(Long userId) {
        Set<Long> eventIds = analyzerClient.getRecommendationsForUser(userId, 10)
                .map(RecommendedEventProto::getEventId).collect(Collectors.toSet());

        return eventRepository
                .findAllByIdIn(eventIds)
                .stream()
                .map(EventMapper::mapToEventDto)
                .toList();
    }

    @Override
    public EventDto getEventOfUser(PrivateEventParam param) {
        return EventMapper.mapToEventDto(checkEvent(param.getEventId(), param.getUserId()));
    }

    @Transactional
    @Override
    public EventDto createEvent(PrivateEventParam param) {
        log.debug("получили параметры для создания события {}", param);
        NewEventDto eventFromRequest = param.getNewEvent();
        long userId = param.getUserId();
        userOperations.isExistsUser(userId);
        Long categoryId = eventFromRequest.getCategory();
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException(String.format("Категория id = %d не найдена", categoryId))
        );
        Location location = locationRepository.save(EventMapper.mapFromRequest(eventFromRequest.getLocation()));
        checkEventTime(eventFromRequest.getEventDate());
        Event newEvent = eventRepository.save(EventMapper.mapFromRequest(eventFromRequest));
        newEvent.setCategory(category);
        newEvent.setInitiatorId(userId);
        newEvent.setLocation(location);
        log.debug("имеем новое событие перед маппером {}", newEvent);
        log.debug("выгружаем все события из базы {}", eventRepository.findAll());
        return EventMapper.mapToEventDto(newEvent);
    }

    @Transactional
    @Override
    public void addConfirmedRequest(long eventId) {
        Event event = checkEvent(eventId);
        event.increaseCountOfConfirmedRequest();
        eventRepository.save(event);
    }

    @Transactional
    @Override
    public void removeConfirmedRequest(long eventId) {
        Event event = checkEvent(eventId);
        event.decreaseCountOfConfirmedRequest();
        eventRepository.save(event);
    }

    @Transactional
    @Override
    public EventDto updateEvent(PrivateEventParam param) {
        long eventId = param.getEventId();
        long userId = param.getUserId();
        Event oldEvent = checkEvent(eventId, userId);
        if (oldEvent.getState().toString().equalsIgnoreCase("PUBLISHED"))
            throw new ConflictException("Событие в публикации не может быть изменено");
        UpdateEventUserRequest eventFromRequest = param.getEventOnUpdate();
        if (eventFromRequest.getEventDate() != null)
            checkEventTime(eventFromRequest.getEventDate());
        Event newEvent = EventMapper.updatePrivateEventFields(oldEvent, eventFromRequest);
        eventRepository.save(newEvent);
        return EventMapper.mapToEventDto(newEvent);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOfUser(PrivateEventParam param) {
        return requestOperations.getRequestsOfUser(param.getUserId(), param.getEventId());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateStatusOfRequests(PrivateEventParam param) {
        long userId = param.getUserId();
        userOperations.isExistsUser(userId);
        long eventId = param.getEventId();
        Event event = checkEvent(eventId, userId);
        EventRequestStatusUpdateRequest requestOnUpdateStatus = param.getRequest();
        EventRequestStatusUpdateResult updatedRequests = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();

        if ((event.getParticipantLimit() == 0 || !event.getRequestModeration())
                && requestOnUpdateStatus.getStatus().equalsIgnoreCase("confirmed"))
            return updatedRequests;

        for (Long requestId : requestOnUpdateStatus.getRequestIds()) {
            ParticipationRequestDto request = requestOperations.getRequestById(requestId);

            if (!request.getStatus().equalsIgnoreCase(RequestState.PENDING.toString()))
                throw new ConflictException("Статус можно изменить только у заявок, находящихся в ожидании");

            if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
                requestOperations.updateRequestStatus(requestId, RequestState.REJECTED);
                request.setStatus("REJECTED");
            }

            if (requestOnUpdateStatus.getStatus().equalsIgnoreCase("confirmed")) {
                if (event.getConfirmedRequests() >= event.getParticipantLimit())
                    throw new ConflictException("Достигнут лимит подтверждённых запросов на участие в событии");
                requestOperations.updateRequestStatus(requestId, RequestState.CONFIRMED);
                request.setStatus("CONFIRMED");
                event.increaseCountOfConfirmedRequest();
                eventRepository.save(event);
                updatedRequests.addConfirmedRequest(request);
            } else if (requestOnUpdateStatus.getStatus().equalsIgnoreCase("rejected")) {
                requestOperations.updateRequestStatus(requestId, RequestState.REJECTED);
                request.setStatus("REJECTED");
                updatedRequests.addRejectedRequest(request);
            } else
                throw new ValidationException("Заявки можно только подтверждать или отклонять");
        }
        return updatedRequests;
    }

    @Override
    public Collection<EventDto> getPublicAllEvents(EventFilter filter, HttpServletRequest request) {
        log.debug("параметры для фильтрации {}", filter);
        log.debug("все события что есть в бд {}", eventRepository.findAll());
        checkFilterDateRangeIsGood(filter.getRangeStart(), filter.getRangeEnd());
        List<Event> events;
        QEvent event = QEvent.event;
        BooleanExpression exp;
        exp = event.state.eq(EventState.PUBLISHED);
        if (filter.getText() != null && !filter.getText().isBlank()) {
            exp = exp.and(event.description.containsIgnoreCase(filter.getText()))
                    .or(event.annotation.containsIgnoreCase(filter.getText()));
        }
        if (filter.getCategories() != null) {
            exp = exp.and(event.category.id.in(filter.getCategories()));
        }
        if (filter.getPaid() != null) {
            exp = exp.and(event.paid.eq(filter.getPaid()));
        }
        if (filter.getRangeStart() != null) {
            exp = exp.and(event.eventDate.after(filter.getRangeStart()));
        }
        if (filter.getRangeEnd() != null) {
            exp = exp.and(event.eventDate.before(filter.getRangeEnd()));
        }
        if (filter.getOnlyAvailable()) {
            exp = exp.and(event.participantLimit.gt(event.confirmedRequests));
        }
        log.debug("sql запрос к бд {}", exp);
        JPAQuery<Event> query = queryFactory.selectFrom(event)
                .where(exp)
                .offset(filter.getFrom())
                .limit(filter.getSize());
        events = query.fetch();
        log.debug("события получаемы из бд после фильтрации {}", events);
        return events.stream()
                .map(EventMapper::mapToEventDto)
                .sorted((e1, e2) -> filter.getSort() == null || filter.getSort().equals("EVENT_DATE") ?
                        e1.getEventDate().compareTo(e2.getEventDate()) : e1.getRating().compareTo(e2.getRating()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Collection<EventDto> getAdminAllEvents(EventFilter filter) {
        log.debug("параметры для фильтрации {}", filter);
        log.debug("все события что есть в бд {}", eventRepository.findAll());
        checkFilterDateRangeIsGood(filter.getRangeStart(), filter.getRangeEnd());
        List<Event> events;
        QEvent event = QEvent.event;
        BooleanExpression exp = Expressions.asBoolean(true).isTrue();
        if (filter.getStates() != null) {
            List<EventState> eventStates = filter.getStates().stream()
                    .map(EventState::valueOf)
                    .toList();
            exp = event.state.in(eventStates);
        }
        if (filter.getUsers() != null) {
            exp = exp.and(event.initiatorId.in(filter.getUsers()));
        }
        if (filter.getCategories() != null) {
            exp = exp.and(event.category.id.in(filter.getCategories()));
        }
        if (filter.getRangeStart() != null) {
            exp = exp.and(event.eventDate.after(filter.getRangeStart()));
        }
        if (filter.getRangeEnd() != null) {
            exp = exp.and(event.eventDate.before(filter.getRangeEnd()));
        }
        log.debug("sql запрос к бд {}", exp);
        JPAQuery<Event> query = queryFactory.selectFrom(event)
                .where(exp)
                .offset(filter.getFrom())
                .limit(filter.getSize());
        events = query.fetch();
        log.debug("события получаемы из бд после фильтрации {}", events);
        return events.stream()
                .map(event1 -> {
                    EventDto eventFullDto = EventMapper.mapToEventDto(event1);
                    eventFullDto.setInitiator(userOperations.getUserById(event1.getInitiatorId()));
                    return eventFullDto;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public EventDto getPublicEvent(Long userId, Long eventId, HttpServletRequest request) {
        final Event event = checkEvent(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException(String.format("Событие id = %d не является публичным", eventId));
        }

        return EventMapper.mapToEventDto(event);
    }

    @Transactional
    @Override
    public EventDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        Event event = checkEvent(eventId);
        log.debug("событие что мы нашли {}", event);
        log.debug("сам запрос на обновление {}", updateEvent);
        validateEventDateForAdmin(updateEvent.getEventDate() == null ? event.getEventDate() :
                updateEvent.getEventDate(), updateEvent.getStateAction());
        validateStatusForAdmin(event.getState(), updateEvent.getStateAction());
        if (updateEvent.getLocation() != null) {
            Location newLocation = locationRepository.save(EventMapper.mapFromRequest(updateEvent.getLocation()));
            log.debug("сохранили новую локацию {}", newLocation);
            event.setLocation(newLocation);
        }
        EventMapper.updateAdminEventFields(event, updateEvent);
        if (event.getState() != null && event.getState().equals(EventState.PUBLISHED)) {
            event.setPublishedOn(LocalDateTime.now());
        }
        eventRepository.save(event);
        log.debug("обновленное событие {}", event);
        return EventMapper.mapToEventDto(event);
    }

    @Override
    public List<CommentDto> getCommentsOfUser(PrivateCommentParam param) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(param.getFrom(), param.getSize(), sortById);

        QComment qComment = QComment.comment;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(QComment.comment.event.id.eq(param.getEventId()));
        conditions.add(QComment.comment.userId.eq(param.getUserId()));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        return CommentMapper.mapToCommentDto(commentRepository.findAll(finalCondition, page)
        );
    }

    @Transactional
    @Override
    public CommentDto createComment(PrivateCommentParam param) {
        log.debug("получили параметры для создания комментария к событию {}", param);
        NewCommentDto commentFromRequest = param.getNewComment();
        long userId = param.getUserId();
        userOperations.isExistsUser(userId);
        long eventId = param.getEventId();
        Event event = checkEvent(eventId);
        Comment newComment = commentRepository.save(CommentMapper.mapFromRequest(commentFromRequest));
        newComment.setUserId(userId);
        newComment.setEvent(event);
        log.debug("имеем новый комментарий перед маппером {}", newComment);
        return CommentMapper.mapToCommentDto(newComment);
    }

    @Override
    public List<CommentDto> getComments(OpenCommentParam param) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(param.getFrom(), param.getSize(), sortById);

        QComment qComment = QComment.comment;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(QComment.comment.event.id.eq(param.getEventId()));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        return CommentMapper.mapToCommentDto(commentRepository.findAll(finalCondition, page));
    }

    @Override
    public CommentDto getCommentById(AdminCommentParam param) {
        Comment comment = commentRepository.findById(param.getCommentId()).orElseThrow(
                () -> new NotFoundException(String.format("Комментарий id = %d не найден", param.getCommentId()))
        );
        return CommentMapper.mapToCommentDto(comment);
    }

    @Transactional
    @Override
    public CommentDto updateComment(AdminCommentParam param) {
        Comment oldComment = commentRepository.findById(param.getCommentId()).orElseThrow(
                () -> new NotFoundException(String.format("Комментарий id = %d не найден", param.getCommentId()))
        );
        UpdateCommentDto commentOnUpdate = param.getComment();
        if (commentOnUpdate.hasDescription()) {
            oldComment.setDescription(commentOnUpdate.getDescription());
        }
        commentRepository.save(oldComment);
        return CommentMapper.mapToCommentDto(oldComment);
    }

    @Transactional
    @Override
    public void removeComment(AdminCommentParam param) {
        long commentId = param.getCommentId();
        if (commentRepository.findById(commentId).isEmpty())
            throw new NotFoundException(String.format("Комментарий id = %d не найден", commentId));
        commentRepository.deleteById(commentId);
    }

    private void checkFilterDateRangeIsGood(LocalDateTime dateBegin, LocalDateTime dateEnd) {
        if (dateBegin == null) {
            return;
        }
        if (dateEnd == null) {
            return;
        }

        if (dateBegin.isAfter(dateEnd)) {
            throw new ValidationException("Неверно задана дата начала и конца события в фильтре");
        }
    }

    private Event checkEvent(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Событие id = %d не найдено", eventId))
        );
    }

    private Event checkEvent(long eventId, long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new NotFoundException(
                String.format("Событие id = %d у пользователя id = %d не найдено", eventId, userId))
        );
    }

    private void validateEventDateForAdmin(LocalDateTime eventDate, String stateAction) {
        if (stateAction != null && stateAction.equals("PUBLISH_EVENT") &&
                eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("Прошло более часа с момента публикации события");
        }
        if (eventDate.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата события не может быть в прошлом");
        }
    }

    private void validateStatusForAdmin(EventState eventState, String stateAction) {
        if (!eventState.equals(EventState.PENDING) && stateAction.equals("PUBLISH_EVENT")) {
            throw new ConflictException("Событие не в ожидании публикации");
        }
        if (eventState.equals(EventState.PUBLISHED) && stateAction.equals("REJECT_EVENT")) {
            throw new ConflictException("Нельзя отклонить опубликованное событие");
        }
    }

    private void checkEventTime(String eventDateStr) {
        LocalDateTime eventDate = LocalDateTime.parse(eventDateStr, formatter);
        Duration duration = Duration.between(LocalDateTime.now(), eventDate);
        Duration minDuration = duration.minusHours(2);
        if (minDuration.isNegative() && !minDuration.isZero()) {
            throw new WrongTimeEventException(
                    "Событие должно наступить минимум через 2 часа от момента добавления события");
        }
    }
}
