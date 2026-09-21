package com.thanhhandsome.pythonmaster.service;

import com.thanhhandsome.pythonmaster.dto.response.analytics.SalesAnalyticsResponse;

import java.time.LocalDate;

public interface SalesAnalyticsUseCase {
    SalesAnalyticsResponse getAnalytics(Long contestId, LocalDate from, LocalDate to, Long saleId);
}
