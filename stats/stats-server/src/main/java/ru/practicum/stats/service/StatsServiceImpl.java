package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.NewHitDto;
import ru.practicum.dto.ViewDto;
import ru.practicum.stats.controller.StatsParam;
import ru.practicum.stats.exception.ValidationException;
import ru.practicum.stats.mapper.HitMapper;
import ru.practicum.stats.mapper.ViewMapper;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.model.View;
import ru.practicum.stats.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final HitRepository hitRepository;

    @Override
    @Transactional
    public HitDto saveHit(NewHitDto newHit) {
        Hit hit = hitRepository.save(HitMapper.mapFromRequest(newHit));
        return HitMapper.mapToHitDto(hit);
    }

    @Override
    public List<ViewDto> getViews(StatsParam param) {
        LocalDateTime start = param.getStart();
        LocalDateTime end = param.getEnd();

        if (start.isAfter(end))
            throw new ValidationException("Дата начала не может быть позже даты конца диапазона");

        List<Hit> hits;
        List<String> uris = param.getUris();
        boolean isUnique = param.isUnique();
        if (uris == null) {
            hits = hitRepository.findAllHitsWithoutUris(start, end);
            if (isUnique)
                hits = hits.stream()
                        .distinct()
                        .toList();
        } else {
            hits = hitRepository.findAllHitsWithUris(start, end, uris);
            if (isUnique)
                hits = hits.stream()
                        .distinct()
                        .toList();
        }
        List<View> views = hits.stream()
                .map(ViewMapper::mapToView)
                .distinct()
                .peek(view -> {
                    if (isUnique)
                        view.setHits(1);
                    else
                        view.setHits(hitRepository.findCountViews(view.getApp(), view.getUri()));
                })
                .sorted(Comparator.comparing(View::getHits).reversed())
                .toList();
        return ViewMapper.mapToViewDto(views);
    }
}
