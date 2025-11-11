package ru.practicum.request.param;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.api.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.api.enums.RequestState;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrivateRequestParam {
    Long userId;
    Long eventId;
    Long requestId;
    EventRequestStatusUpdateRequest request;
    RequestState requestState;
}
