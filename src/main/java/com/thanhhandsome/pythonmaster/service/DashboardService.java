package com.thanhhandsome.pythonmaster.service;

import com.thanhhandsome.pythonmaster.dto.request.dashboard.DashboardFilterRequest;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardDemographicsResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardFunnelResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardKpiResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardRevenueResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardTopPartnersResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardTrendResponse;
import com.thanhhandsome.pythonmaster.service.dashboard.DashboardDemographicsService;
import com.thanhhandsome.pythonmaster.service.dashboard.DashboardFunnelService;
import com.thanhhandsome.pythonmaster.service.dashboard.DashboardKpiService;
import com.thanhhandsome.pythonmaster.service.dashboard.DashboardPartnerService;
import com.thanhhandsome.pythonmaster.service.dashboard.DashboardRevenueService;
import com.thanhhandsome.pythonmaster.service.dashboard.DashboardTrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService implements DashboardUseCase {
    private final DashboardKpiService dashboardKpiService;
    private final DashboardFunnelService dashboardFunnelService;
    private final DashboardDemographicsService dashboardDemographicsService;
    private final DashboardPartnerService dashboardPartnerService;
    private final DashboardTrendService dashboardTrendService;
    private final DashboardRevenueService dashboardRevenueService;

    @Override
    public DashboardKpiResponse getKpis(DashboardFilterRequest filter) {
        return dashboardKpiService.getKpis(filter);
    }

    @Override
    public DashboardFunnelResponse getConversionFunnel(DashboardFilterRequest filter) {
        return dashboardFunnelService.getConversionFunnel(filter);
    }

    @Override
    public DashboardDemographicsResponse getDemographics(DashboardFilterRequest filter) {
        return dashboardDemographicsService.getDemographics(filter);
    }

    @Override
    public DashboardTopPartnersResponse getTopPartners(DashboardFilterRequest filter, Integer limit) {
        return dashboardPartnerService.getTopPartners(filter, limit);
    }

    @Override
    public DashboardTrendResponse getRegistrationTrend(DashboardFilterRequest filter) {
        return dashboardTrendService.getRegistrationTrend(filter);
    }

    @Override
    public DashboardRevenueResponse getRevenue(DashboardFilterRequest filter) {
        return dashboardRevenueService.getRevenue(filter);
    }
}
