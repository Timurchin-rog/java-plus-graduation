package ru.practicum.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {

    Long id;

    Long event;

    Long requester;

    String status;

    String created;
}
