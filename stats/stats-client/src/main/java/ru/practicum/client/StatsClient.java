package ru.practicum.client;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.NewHitDto;
import ru.practicum.dto.ViewDto;

import java.util.List;

@Slf4j
@Service
public class StatsClient {
    private final String statsServerUrl;
    private final RestClient restClient;

    public StatsClient(@Value("${stats:server:url}") String statsServerUrl) {
        this.statsServerUrl = statsServerUrl;
        this.restClient = RestClient.create(statsServerUrl);
    }

    // Регистрация "хита"
    public void registerHit(@Valid NewHitDto hitDto) {
        try {
            restClient.post()
                    .uri(statsServerUrl + "/hit")
                    .body(hitDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("ошибка при отправке hit");
        }
    }

    // Получение статистики
    public List<ViewDto> getStats(
            String start,
            String end,
            List<String> uris,
            boolean unique
    ) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(statsServerUrl + "/stats")
                    .queryParam("start", start)
                    .queryParam("end", end)
                    .queryParam("uris", uris)
                    .queryParam("unique", unique)
                    .build()
                    .toUriString();

            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientException e) {
            log.error("Ошибка при получении статистики.");
        }
        return List.of();
    }
}
