package ru.practicum.request.service;

import ru.practicum.api.dto.request.ParticipationRequestDto;
import ru.practicum.request.param.PrivateRequestParam;

import java.util.List;
import java.util.Map;

public interface RequestService {

    List<ParticipationRequestDto> getRequestOfCurrentUser(PrivateRequestParam param);

    List<ParticipationRequestDto> getRequestsOfUser(PrivateRequestParam param);

    ParticipationRequestDto getRequestById(long requestId);

    ParticipationRequestDto updateRequestStatus(PrivateRequestParam param);

    ParticipationRequestDto createRequest(PrivateRequestParam param);

    ParticipationRequestDto updateRequest(PrivateRequestParam param);

    Map<Long, Long> getConfirmedEventsRequestsCount(List<Long> eventsIds);
}
