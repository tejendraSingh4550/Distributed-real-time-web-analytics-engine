package com.analytics.engine.service;

import com.analytics.engine.dto.WebEvent;
import com.analytics.engine.entity.AnalyticsEntity;
import com.analytics.engine.repository.AnalyticsRepository;
import java.time.Instant;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@Profile("local")
public class LocalAnalyticsEventPublisher implements AnalyticsEventPublisher {

    private final AnalyticsRepository analyticsRepository;

    public LocalAnalyticsEventPublisher(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    @Override
    public Mono<Void> publish(WebEvent event) {
        return Mono.fromRunnable(() -> analyticsRepository.save(new AnalyticsEntity(
                        event.trackingId(),
                        event.eventType(),
                        event.pageUrl(),
                        event.userId(),
                        event.ipAddress(),
                        event.userAgent(),
                        Instant.ofEpochMilli(event.timestamp())
                )))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
