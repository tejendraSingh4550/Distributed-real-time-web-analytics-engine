package com.analytics.engine.service;

import com.analytics.engine.dto.AnalyticsDashboardResponse;
import com.analytics.engine.dto.AnalyticsDashboardResponse.PageviewBucket;
import com.analytics.engine.dto.AnalyticsDashboardResponse.RecentEvent;
import com.analytics.engine.entity.AnalyticsEntity;
import com.analytics.engine.repository.AnalyticsRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class LocalAnalyticsDashboardProvider implements AnalyticsDashboardProvider {

    private static final long BUCKET_SECONDS = 5 * 60;
    private static final long DAY_SECONDS = 24 * 60 * 60;

    private final AnalyticsRepository analyticsRepository;

    public LocalAnalyticsDashboardProvider(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    @Override
    public AnalyticsDashboardResponse getDashboard(String trackingId) {
        Instant now = Instant.now();
        long currentBucket = now.getEpochSecond() / BUCKET_SECONDS * BUCKET_SECONDS;
        Instant dayAgo = now.minusSeconds(DAY_SECONDS);
        List<AnalyticsEntity> events = analyticsRepository.findByTrackingIdAndEventTimeAfter(trackingId, dayAgo);

        Map<Long, Long> pageviewsByBucket = new TreeMap<>();
        for (long bucket = currentBucket - DAY_SECONDS; bucket <= currentBucket; bucket += BUCKET_SECONDS) {
            pageviewsByBucket.put(bucket, 0L);
        }

        long pageviews = 0;
        Set<String> activeUsers = new HashSet<>();
        Instant activeSince = now.minusSeconds(15 * 60);
        for (AnalyticsEntity event : events) {
            if ("pageview".equalsIgnoreCase(event.getEventType())) {
                pageviews++;
                long bucket = event.getEventTime().getEpochSecond() / BUCKET_SECONDS * BUCKET_SECONDS;
                pageviewsByBucket.computeIfPresent(bucket, (ignored, count) -> count + 1);
            }
            if (event.getUserId() != null && !event.getEventTime().isBefore(activeSince)) {
                activeUsers.add(event.getUserId());
            }
        }

        List<PageviewBucket> buckets = new ArrayList<>(pageviewsByBucket.size());
        pageviewsByBucket.forEach((bucket, count) -> buckets.add(
                new PageviewBucket(Instant.ofEpochSecond(bucket).toString(), count)));
        List<RecentEvent> recentEvents = analyticsRepository
                .findTop10ByTrackingIdOrderByEventTimeDesc(trackingId).stream()
                .map(event -> new RecentEvent(
                        event.getEventType(), event.getPageUrl(), event.getUserId(), event.getEventTime().toString()))
                .toList();
        return new AnalyticsDashboardResponse(pageviews, activeUsers.size(), buckets, recentEvents);
    }
}
