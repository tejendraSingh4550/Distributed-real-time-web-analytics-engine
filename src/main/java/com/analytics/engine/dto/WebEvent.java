package com.analytics.engine.dto;

public record WebEvent(
        String trackingId,
        String eventType,
        String pageUrl,
        String userId,
        String ipAddress,
        String userAgent,
        long timestamp
) {
    public WebEvent withClientDetails(String clientIpAddress, long serverTimestamp) {
        return new WebEvent(
                trackingId,
                eventType,
                pageUrl,
                userId,
                clientIpAddress,
                userAgent,
                serverTimestamp
        );
    }
}
