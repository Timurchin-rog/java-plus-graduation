package ru.practicum.request.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.api.feign.EventOperations;
import ru.practicum.client.CollectorClient;
import ru.practicum.api.dto.event.EventDto;
import ru.practicum.api.dto.request.ParticipationRequestDto;
import ru.practicum.api.enums.EventState;
import ru.practicum.api.enums.RequestState;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.api.exception.ConflictException;
import ru.practicum.api.exception.NotFoundException;
import ru.practicum.api.feign.UserOperations;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.QRequest;
import ru.practicum.request.model.Request;
import ru.practicum.request.param.PrivateRequestParam;
import ru.practicum.request.repository.RequestRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventOperations eventClient;
    private final UserOperations userOperations;
    private final CollectorClient collectorClient;

    @Override
    public List<ParticipationRequestDto> getRequestOfCurrentUser(PrivateRequestParam param) {
        QRequest qRequest = QRequest.request;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(QRequest.request.requesterId.eq(param.getUserId()));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        return RequestMapper.mapToRequestDto(requestRepository.findAll(finalCondition));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOfUser(PrivateRequestParam param) {
        QRequest qRequest = QRequest.request;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(QRequest.request.eventId.eq(param.getEventId()));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        return RequestMapper.mapToRequestDto(requestRepository.findAll(finalCondition));
    }

    @Override
    public ParticipationRequestDto getRequestById(long requestId) {
        return RequestMapper.mapToRequestDto(checkRequest(requestId));
    }

    @Transactional
    @Override
    public ParticipationRequestDto updateRequestStatus(PrivateRequestParam param) {
        Request request = checkRequest(param.getRequestId());
        request.setState(param.getRequestState());
        Request updatedRequest = requestRepository.save(request);
        return RequestMapper.mapToRequestDto(updatedRequest);
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(PrivateRequestParam param) {
        log.error("Началось создание заявки");
        Long userId = param.getUserId();
        Long eventId = param.getEventId();
        EventDto event = eventClient.getEventApi(eventId);

        if (checkDuplicatedRequest(param))
            throw new ConflictException("Нельзя добавить повторный запрос");
        if (event.getInitiatorId().equals(userId))
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        if (!event.getState().equalsIgnoreCase(EventState.PUBLISHED.toString()))
            throw new ConflictException("Нельзя учавствовать в неопубликованном событии");
        if (event.getConfirmedRequests() >= event.getParticipantLimit()
        && event.getParticipantLimit() != 0)
            throw new ConflictException("Достигнут лимит подтверждённых запросов на участие в событии");

        Request request = new Request(event.getId(), userId);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setState(RequestState.CONFIRMED);
            eventClient.addConfirmedRequest(eventId);
        } else {
            request.setState(RequestState.PENDING);
        }

        collectorClient.collectUserAction(userId, eventId, ActionTypeProto.ACTION_REGISTER, Instant.now());

        Request newRequest = requestRepository.save(request);
        return RequestMapper.mapToRequestDto(newRequest);
    }

    private boolean checkDuplicatedRequest(PrivateRequestParam param) {
        QRequest qRequest = QRequest.request;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(QRequest.request.requesterId.eq(param.getUserId()));
        conditions.add(QRequest.request.eventId.eq(param.getEventId()));

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        Iterable<Request> requestsFromRep = requestRepository.findAll(finalCondition);
        List<ParticipationRequestDto> requestsDto = RequestMapper.mapToRequestDto(requestsFromRep);

        return !requestsDto.isEmpty();
    }

    @Transactional
    @Override
    public ParticipationRequestDto updateRequest(PrivateRequestParam param) {
        userOperations.isExistsUser(param.getUserId());
        long requestId = param.getRequestId();
        Request oldRequest = checkRequest(requestId);
        EventDto event = eventClient.getEventApi(oldRequest.getEventId());

        oldRequest.setState(RequestState.CANCELED);
        eventClient.removeConfirmedRequest(event.getId());

        Request updatedRequest = requestRepository.save(oldRequest);
        return RequestMapper.mapToRequestDto(updatedRequest);
    }
//проблема в методе репозитория
    @Override
    public Map<Long, Long> getConfirmedEventsRequestsCount(List<Long> eventsIds) {
        log.warn("Началось получение мапы с eventId и количеством запросов");
        return requestRepository
                .getCountByEventIdInAndStatus(eventsIds, RequestState.CONFIRMED.toString()).stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> ((Number) row[1]).longValue()
                ));
    }

    private Request checkRequest(long requestId) {
        return requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос id = %d не найден", requestId))
        );
    }

}
