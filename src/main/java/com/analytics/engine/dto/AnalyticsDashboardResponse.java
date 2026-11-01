package com.analytics.engine.dto;

import java.util.List;

public record AnalyticsDashboardResponse(
        long pageviewsLast24Hours,
        long activeUsersLast15Minutes,
        List<PageviewBucket> pageviewsOverTime,
        List<RecentEvent> recentEvents
) {
    public record PageviewBucket(String bucket, long pageviews) {
    }

    public record RecentEvent(String eventType, String pageUrl, String userId, String eventTime) {
    }
}
