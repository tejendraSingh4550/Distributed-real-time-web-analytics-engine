package com.analytics.engine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.SequenceGenerator;
import java.time.Instant;

@Entity
@Table(name = "web_analytics")
public class AnalyticsEntity {

    @Id
    @SequenceGenerator(name = "web_analytics_id_generator", sequenceName = "web_analytics_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "web_analytics_id_generator")
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "tracking_id", nullable = false, length = 255)
    private String trackingId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "page_url", nullable = false, columnDefinition = "TEXT")
    private String pageUrl;

    @Column(name = "user_id", length = 255)
    private String userId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "event_time", nullable = false)
    private Instant eventTime;

    protected AnalyticsEntity() {
    }

    public AnalyticsEntity(String trackingId, String eventType, String pageUrl, String userId,
                           String ipAddress, String userAgent, Instant eventTime) {
        this.trackingId = trackingId;
        this.eventType = eventType;
        this.pageUrl = pageUrl;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.eventTime = eventTime;
    }

    public Long getId() {
        return id;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Instant getEventTime() {
        return eventTime;
    }
}


