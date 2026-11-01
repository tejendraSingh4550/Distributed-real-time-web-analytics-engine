package com.analytics.engine.controller;

import com.analytics.engine.dto.WebEvent;
import com.analytics.engine.dto.AnalyticsDashboardResponse;
import com.analytics.engine.service.AnalyticsDashboardProvider;
import com.analytics.engine.service.AnalyticsEventPublisher;
import java.net.InetSocketAddress;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/v1/analytics")
public class IngestionController {

    private final AnalyticsEventPublisher eventPublisher;
    private final AnalyticsDashboardProvider dashboardProvider;

    public IngestionController(
            AnalyticsEventPublisher eventPublisher,
            AnalyticsDashboardProvider dashboardProvider) {
        this.eventPublisher = eventPublisher;
        this.dashboardProvider = dashboardProvider;
    }

    @GetMapping("/dashboard")
    public Mono<AnalyticsDashboardResponse> dashboard(
            @RequestParam(defaultValue = "site-1") String trackingId) {
        return Mono.fromCallable(() -> dashboardProvider.getDashboard(trackingId))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/collect")
    public Mono<ResponseEntity<Void>> collect(
            @RequestBody WebEvent event,
            ServerWebExchange exchange) {
        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        String clientIp = remoteAddress == null || remoteAddress.getAddress() == null
                ? null
                : remoteAddress.getAddress().getHostAddress();
        WebEvent enrichedEvent = event.withClientDetails(clientIp, System.currentTimeMillis());

        return eventPublisher.publish(enrichedEvent)
                .thenReturn(ResponseEntity.status(HttpStatus.ACCEPTED).<Void>build());
    }
}
