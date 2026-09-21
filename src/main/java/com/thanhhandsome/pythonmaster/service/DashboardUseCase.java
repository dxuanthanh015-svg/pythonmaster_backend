package com.thanhhandsome.pythonmaster.service;

import com.thanhhandsome.pythonmaster.dto.request.dashboard.DashboardFilterRequest;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardDemographicsResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardFunnelResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardKpiResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardRevenueResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardTopPartnersResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardTrendResponse;

public interface DashboardUseCase {
    DashboardKpiResponse getKpis(DashboardFilterRequest filter);
    DashboardFunnelResponse getConversionFunnel(DashboardFilterRequest filter);
    DashboardDemographicsResponse getDemographics(DashboardFilterRequest filter);
    DashboardTopPartnersResponse getTopPartners(DashboardFilterRequest filter, Integer limit);
    DashboardTrendResponse getRegistrationTrend(DashboardFilterRequest filter);
    DashboardRevenueResponse getRevenue(DashboardFilterRequest filter);
}
