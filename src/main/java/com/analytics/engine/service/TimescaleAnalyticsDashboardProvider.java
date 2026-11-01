package com.analytics.engine.service;

import com.analytics.engine.dto.AnalyticsDashboardResponse;
import com.analytics.engine.dto.AnalyticsDashboardResponse.PageviewBucket;
import com.analytics.engine.dto.AnalyticsDashboardResponse.RecentEvent;
import com.analytics.engine.entity.AnalyticsEntity;
import com.analytics.engine.repository.AnalyticsRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!local")
public class TimescaleAnalyticsDashboardProvider implements AnalyticsDashboardProvider {

    private final AnalyticsRepository analyticsRepository;

    public TimescaleAnalyticsDashboardProvider(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    @Override
    public AnalyticsDashboardResponse getDashboard(String trackingId) {
        List<PageviewBucket> buckets = analyticsRepository.getPageviewsOverTime(trackingId).stream()
                .map(row -> new PageviewBucket(toInstant(row[0]).toString(), ((Number) row[1]).longValue()))
                .toList();
        long pageviews = buckets.stream().mapToLong(PageviewBucket::pageviews).sum();
        List<RecentEvent> recentEvents = analyticsRepository
                .findTop10ByTrackingIdOrderByEventTimeDesc(trackingId).stream()
                .map(this::toRecentEvent)
                .toList();
        return new AnalyticsDashboardResponse(
                pageviews,
                analyticsRepository.getLiveActiveUsers(trackingId),
                buckets,
                recentEvents
        );
    }

    private RecentEvent toRecentEvent(AnalyticsEntity event) {
        return new RecentEvent(
                event.getEventType(), event.getPageUrl(), event.getUserId(), event.getEventTime().toString());
    }

    private Instant toInstant(Object value) {
        if (value instanceof Instant instant) {
            return instant;
        }
        if (value instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime.toInstant();
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toInstant();
        }
        return Instant.parse(value.toString());
    }
}
