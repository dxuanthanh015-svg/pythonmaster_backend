package com.thanhhandsome.pythonmaster.controller;

import com.thanhhandsome.pythonmaster.dto.request.dashboard.DashboardFilterRequest;
import com.thanhhandsome.pythonmaster.dto.response.ApiResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardDemographicsResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardFunnelResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardKpiResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardRevenueResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardTopPartnersResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardTrendResponse;
import com.thanhhandsome.pythonmaster.service.DashboardUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "KPI, funnel, demographics, partners, trend, revenue")
public class DashboardController {
    private final DashboardUseCase dashboardUseCase;

    /** Các KPI tổng quan: đăng ký, tỷ lệ chuyển đổi, doanh thu, lead follow-up, target gap */
    @GetMapping("/kpis")
    public ResponseEntity<ApiResponse<DashboardKpiResponse>> getKpis(
            @Valid @ModelAttribute DashboardFilterRequest filter) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy dữ liệu KPI thành công", dashboardUseCase.getKpis(filter))
        );
    }

    /** Phễu chuyển đổi: Tiếp cận → Đăng ký → Tham gia */
    @GetMapping("/conversion-funnel")
    public ResponseEntity<ApiResponse<DashboardFunnelResponse>> getConversionFunnel(
            @Valid @ModelAttribute DashboardFilterRequest filter) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy dữ liệu phễu chuyển đổi thành công",
                        dashboardUseCase.getConversionFunnel(filter))
        );
    }

    /** Nhân khẩu học: bảng thi (A/B) và phân bố khu vực */
    @GetMapping("/demographics")
    public ResponseEntity<ApiResponse<DashboardDemographicsResponse>> getDemographics(
            @Valid @ModelAttribute DashboardFilterRequest filter) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy dữ liệu nhân khẩu học thành công",
                        dashboardUseCase.getDemographics(filter))
        );
    }

    /** Top đối tác/đơn vị giới thiệu nhiều thí sinh nhất */
    @GetMapping("/top-partners")
    public ResponseEntity<ApiResponse<DashboardTopPartnersResponse>> getTopPartners(
            @Valid @ModelAttribute DashboardFilterRequest filter,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy dữ liệu top đối tác thành công",
                        dashboardUseCase.getTopPartners(filter, limit))
        );
    }

    /** Xu hướng đăng ký theo ngày — dùng cho biểu đồ line chart */
    @GetMapping("/registration-trend")
    public ResponseEntity<ApiResponse<DashboardTrendResponse>> getRegistrationTrend(
            @Valid @ModelAttribute DashboardFilterRequest filter) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy dữ liệu xu hướng đăng ký thành công",
                        dashboardUseCase.getRegistrationTrend(filter))
        );
    }

    /** Doanh thu tổng và chi tiết theo bảng thi (A/B) — phục vụ BarChart Recharts */
    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<DashboardRevenueResponse>> getRevenue(
            @Valid @ModelAttribute DashboardFilterRequest filter) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy dữ liệu doanh thu thành công",
                        dashboardUseCase.getRevenue(filter))
        );
    }
}
