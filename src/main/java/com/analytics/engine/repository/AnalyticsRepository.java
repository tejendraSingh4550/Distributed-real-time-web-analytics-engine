package com.analytics.engine.repository;

import com.analytics.engine.entity.AnalyticsEntity;
import java.util.List;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnalyticsRepository extends JpaRepository<AnalyticsEntity, Long> {

    List<AnalyticsEntity> findByTrackingIdAndEventTimeAfter(String trackingId, Instant eventTime);

    List<AnalyticsEntity> findTop10ByTrackingIdOrderByEventTimeDesc(String trackingId);

    @Query(value = """
            SELECT time_bucket(INTERVAL '5 minutes', event_time) AS bucket,
                   COUNT(*) AS pageviews
            FROM web_analytics
            WHERE tracking_id = :trackingId
              AND event_type = 'pageview'
              AND event_time >= NOW() - INTERVAL '24 hours'
            GROUP BY bucket
            ORDER BY bucket
            """, nativeQuery = true)
    List<Object[]> getPageviewsOverTime(@Param("trackingId") String trackingId);

    @Query(value = """
            SELECT COUNT(DISTINCT user_id)
            FROM web_analytics
            WHERE tracking_id = :trackingId
              AND event_time >= NOW() - INTERVAL '15 minutes'
            """, nativeQuery = true)
    long getLiveActiveUsers(@Param("trackingId") String trackingId);
}
