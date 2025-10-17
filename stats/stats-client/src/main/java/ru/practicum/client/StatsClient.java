package ru.practicum.client;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
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
    private final DiscoveryClient discoveryClient;
    private final String statsServiceId;
    private final RestClient restClient;

    public StatsClient(DiscoveryClient discoveryClient,
                       @Value("${discovery:services:stats-server-id}") String statsServiceId) {
        this.discoveryClient = discoveryClient;
        this.statsServiceId = statsServiceId;
        this.restClient = RestClient.create(statsServiceId);
    }

    // Регистрация "хита"
    public void registerHit(@Valid NewHitDto hitDto) {
        try {
            restClient.post()
                    .uri(getInstance() + "/hit")
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
            String url = UriComponentsBuilder.fromHttpUrl(getInstance() + "/stats")
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

    private ServiceInstance getInstance() {
        try {
            return discoveryClient
                    .getInstances(statsServiceId)
                    .getFirst();
        } catch (Exception exception) {
            throw new StatsServerUnavailable("Ошибка обнаружения адреса сервиса статистики");
        }
    }
}
