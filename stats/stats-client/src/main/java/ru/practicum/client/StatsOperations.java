package ru.practicum.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.NewHitDto;
import ru.practicum.dto.ViewDto;

import java.util.List;

@FeignClient(name = "stats-server")
public interface StatsOperations {

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    HitDto saveHit(@Valid @RequestBody NewHitDto newHitDto);

    @GetMapping(path = "/stats")
    List<ViewDto> getViews(@RequestParam String start,
                           @RequestParam String end,
                           @RequestParam(required = false) List<String> uris,
                           @RequestParam(defaultValue = "false") boolean unique);
}
