package com.thanhhandsome.pythonmaster.dto.response.analytics;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalesAnalyticsResponse {
    Long contestId;
    LocalDate from;
    LocalDate to;
    FunnelResponse funnel;
    PaymentKpiResponse paymentKpi;
    List<SaleKpiResponse> sales;
}
