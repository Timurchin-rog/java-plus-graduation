package ru.practicum.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class StatsServerUnavailable extends RuntimeException {
     public StatsServerUnavailable(String message) {
         super(message);
     }
}
