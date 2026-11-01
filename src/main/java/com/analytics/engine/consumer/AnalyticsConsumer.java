package com.analytics.engine.consumer;

import com.analytics.engine.dto.WebEvent;
import com.analytics.engine.entity.AnalyticsEntity;
import com.analytics.engine.repository.AnalyticsRepository;
import java.time.Instant;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!local")
public class AnalyticsConsumer {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsConsumer(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    @KafkaListener(
            topics = "web-analytics-events",
            groupId = "analytics-processors",
            containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void consume(List<ConsumerRecord<String, WebEvent>> records) {
        List<AnalyticsEntity> entities = records.stream()
                .map(ConsumerRecord::value)
                .map(this::toEntity)
                .toList();
        analyticsRepository.saveAll(entities);
    }

    private AnalyticsEntity toEntity(WebEvent event) {
        return new AnalyticsEntity(
                event.trackingId(),
                event.eventType(),
                event.pageUrl(),
                event.userId(),
                event.ipAddress(),
                event.userAgent(),
                Instant.ofEpochMilli(event.timestamp())
        );
    }
}
