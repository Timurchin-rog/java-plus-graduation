package ru.practicum.request.param;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.enums.RequestState;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrivateRequestParam {
    long userId;
    long eventId;
    long requestId;
    EventRequestStatusUpdateRequest request;
    RequestState requestState;
}
