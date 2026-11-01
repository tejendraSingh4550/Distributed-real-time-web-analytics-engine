package com.analytics.engine.service;

import com.analytics.engine.dto.WebEvent;
import reactor.core.publisher.Mono;

public interface AnalyticsEventPublisher {
    Mono<Void> publish(WebEvent event);
}
