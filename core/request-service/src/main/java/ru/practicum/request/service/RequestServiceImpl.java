package ru.practicum.request.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.enums.EventState;
import ru.practicum.enums.RequestState;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.feign.EventClient;
import ru.practicum.feign.UserClient;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.QRequest;
import ru.practicum.request.model.Request;
import ru.practicum.request.param.PrivateRequestParam;
import ru.practicum.request.repository.RequestRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventClient eventClient;
    private final UserClient userClient;

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
        long userId = param.getUserId();
        long eventId = param.getEventId();
        EventFullDto event = eventClient.getEventById(eventId);

        if (checkDuplicatedRequest(param))
            throw new ConflictException("Нельзя добавить повторный запрос");
        if (event.getInitiatorId() == userId)
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
        userClient.isExistUser(param.getUserId());
        long requestId = param.getRequestId();
        Request oldRequest = checkRequest(requestId);
        EventFullDto event = eventClient.getEventById(oldRequest.getEventId());

        oldRequest.setState(RequestState.CANCELED);
        eventClient.removeConfirmedRequest(event.getId());

        Request updatedRequest = requestRepository.save(oldRequest);
        return RequestMapper.mapToRequestDto(updatedRequest);
    }

    private Request checkRequest(long requestId) {
        return requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос id = %d не найден", requestId))
        );
    }

}
