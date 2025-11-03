package ru.practicum.request.service;

import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.request.param.PrivateRequestParam;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getRequestOfCurrentUser(PrivateRequestParam param);

    List<ParticipationRequestDto> getRequestsOfUser(PrivateRequestParam param);

    ParticipationRequestDto getRequestById(long requestId);

    ParticipationRequestDto updateRequestStatus(PrivateRequestParam param);

    ParticipationRequestDto createRequest(PrivateRequestParam param);

    ParticipationRequestDto updateRequest(PrivateRequestParam param);
}
