package com.analytics.engine.service;

import com.analytics.engine.dto.WebEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Profile("!local")
public class KafkaAnalyticsEventPublisher implements AnalyticsEventPublisher {

    private static final String TOPIC = "web-analytics-events";

    private final ReactiveKafkaProducerTemplate<String, WebEvent> producerTemplate;

    public KafkaAnalyticsEventPublisher(ReactiveKafkaProducerTemplate<String, WebEvent> producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @Override
    public Mono<Void> publish(WebEvent event) {
        return producerTemplate.send(TOPIC, event.trackingId(), event).then();
    }
}
