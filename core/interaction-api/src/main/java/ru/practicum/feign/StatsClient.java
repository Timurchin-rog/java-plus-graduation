package ru.practicum.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.stats.NewHitDto;
import ru.practicum.dto.stats.ViewDto;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "stats-server")
public interface StatsClient {

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    void saveHit(@Valid @RequestBody NewHitDto newHitDto);

    @GetMapping(path = "/stats")
    List<ViewDto> getViews(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                           @RequestParam(required = false) List<String> uris,
                           @RequestParam(defaultValue = "false") boolean unique);
}
