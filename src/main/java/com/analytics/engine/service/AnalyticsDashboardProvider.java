package com.analytics.engine.service;

import com.analytics.engine.dto.AnalyticsDashboardResponse;

public interface AnalyticsDashboardProvider {
    AnalyticsDashboardResponse getDashboard(String trackingId);
}
