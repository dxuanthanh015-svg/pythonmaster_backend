package com.thanhhandsome.pythonmaster.controller;

import com.thanhhandsome.pythonmaster.dto.request.dashboard.DashboardFilterRequest;
import com.thanhhandsome.pythonmaster.dto.response.ApiResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardDemographicsResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardFunnelResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardKpiResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardProvincePerformanceResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardRevenueResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardTopPartnersResponse;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardTrendResponse;
import com.thanhhandsome.pythonmaster.repository.DangKyRepository;
import com.thanhhandsome.pythonmaster.repository.ThiSinhRepository;
import com.thanhhandsome.pythonmaster.service.DashboardUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "KPI, funnel, demographics, partners, trend, revenue")
public class DashboardController {

    private final DashboardUseCase dashboardUseCase;
    private final ThiSinhRepository thiSinhRepository;
    private final DangKyRepository dangKyRepository;

    /** Các KPI tổng quan: đăng ký, tỷ lệ chuyển đổi, doanh thu, lead follow-up, target gap */
    @GetMapping("/kpis")
    public ResponseEntity<ApiResponse<DashboardKpiResponse>> getKpis(
            @Valid @ParameterObject @ModelAttribute DashboardFilterRequest filter) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy dữ liệu KPI thành công", dashboardUseCase.getKpis(filter))
        );
    }

    /** Phễu chuyển đổi: Tiếp cận → Đăng ký → Tham gia */
    @GetMapping("/conversion-funnel")
    public ResponseEntity<ApiResponse<DashboardFunnelResponse>> getConversionFunnel(
            @Valid @ParameterObject @ModelAttribute DashboardFilterRequest filter) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy dữ liệu phễu chuyển đổi thành công", dashboardUseCase.getConversionFunnel(filter))
        );
    }

    /** Nhân khẩu học: bảng thi (A/B) và phân bố khu vực */
    @GetMapping("/demographics")
    public ResponseEntity<ApiResponse<DashboardDemographicsResponse>> getDemographics(
            @Valid @ParameterObject @ModelAttribute DashboardFilterRequest filter) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy dữ liệu nhân khẩu học thành công", dashboardUseCase.getDemographics(filter))
        );
    }

    /** Thống kê thí sinh theo nhóm tuổi, dựa trên ngày sinh hiện tại. */
    @GetMapping("/age-groups")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getAgeGroups() {
        LocalDate today = LocalDate.now();
        Map<String, Long> groups = new LinkedHashMap<>();
        groups.put("under15", 0L);
        groups.put("15to17", 0L);
        groups.put("18to20", 0L);
        groups.put("over20", 0L);

        for (LocalDate birthDate : thiSinhRepository.findAllNgaySinh()) {
            int age = Period.between(birthDate, today).getYears();
            if (age < 15) {
                groups.compute("under15", (key, count) -> count + 1);
            } else if (age <= 17) {
                groups.compute("15to17", (key, count) -> count + 1);
            } else if (age <= 20) {
                groups.compute("18to20", (key, count) -> count + 1);
            } else {
                groups.compute("over20", (key, count) -> count + 1);
            }
        }

        return ResponseEntity.ok(ApiResponse.success("Lấy thống kê nhóm tuổi thành công", groups));
    }

    /**
     * Hiệu quả theo tỉnh: xác nhận tham gia là các đăng ký có thanh toán paid/completed.
     * Tỷ lệ chuyển đổi luôn được tính lại từ dữ liệu hiện tại: xác nhận / đăng ký × 100.
     */
    @GetMapping("/province-participation")
    public ResponseEntity<ApiResponse<List<DashboardProvincePerformanceResponse>>> getProvinceParticipation(
            @Valid @ParameterObject @ModelAttribute DashboardFilterRequest filter) {
        LocalDateTime from = filter.getFrom() == null ? null : filter.getFrom().atStartOfDay();
        LocalDateTime to = filter.getTo() == null ? null : filter.getTo().plusDays(1).atStartOfDay();

        List<DashboardProvincePerformanceResponse> items = dangKyRepository
                .countProvinceParticipation(filter.getContestId(), from, to, filter.getSaleId())
                .stream()
                .map(row -> {
                    String province = row[0] == null || row[0].toString().isBlank()
                            ? "Chưa cập nhật" : row[0].toString().trim();
                    long registrations = ((Number) row[1]).longValue();
                    long confirmedParticipants = ((Number) row[2]).longValue();
                    BigDecimal conversionRate = registrations == 0
                            ? BigDecimal.ZERO
                            : BigDecimal.valueOf(confirmedParticipants)
                                    .multiply(BigDecimal.valueOf(100))
                                    .divide(BigDecimal.valueOf(registrations), 2, RoundingMode.HALF_UP);
                    return DashboardProvincePerformanceResponse.builder()
                            .province(province)
                            .region(resolveRegion(province))
                            .registrations(registrations)
                            .confirmedParticipants(confirmedParticipants)
                            .conversionRate(conversionRate)
                            .build();
                })
                .toList();

        return ResponseEntity.ok(ApiResponse.success("Lấy thống kê xác nhận tham gia theo tỉnh thành công", items));
    }

    private String resolveRegion(String province) {
        String normalized = Normalizer.normalize(province, Normalizer.Form.NFD)
                .replaceAll("\p{M}", "")
                .replace('đ', 'd')
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim();

        return switch (normalized) {
            case "ha noi", "hai phong", "bac ninh", "thai nguyen", "vinh phuc", "quang ninh",
                 "bac giang", "ha nam", "nam dinh", "ninh binh", "thai binh", "hung yen", "hai duong",
                 "phu tho", "ha giang", "cao bang", "bac kan", "lang son", "tuyen quang", "lao cai",
                 "yen bai", "dien bien", "lai chau", "son la", "hoa binh" -> "Miền Bắc";
            case "thanh hoa", "nghe an", "ha tinh", "quang binh", "quang tri", "thua thien hue", "hue",
                 "da nang", "quang nam", "quang ngai", "binh dinh", "phu yen", "khanh hoa", "ninh thuan",
                 "binh thuan", "kon tum", "gia lai", "dak lak", "dak nong", "lam dong" -> "Miền Trung";
            case "ho chi minh", "tp ho chi minh", "thanh pho ho chi minh", "hcm", "tp hcm", "binh duong",
                 "binh phuoc", "tay ninh", "ba ria vung tau", "dong nai", "long an", "tien giang", "ben tre",
                 "tra vinh", "vinh long", "dong thap", "an giang", "kien giang", "hau giang", "soc trang",
                 "bac lieu", "ca mau", "can tho" -> "Miền Nam";
            default -> "Khác";
        };
    }

    /** Top đối tác/đơn vị giới thiệu nhiều thí sinh nhất */
    @GetMapping("/top-partners")
    public ResponseEntity<ApiResponse<DashboardTopPartnersResponse>> getTopPartners(
            @Valid @ParameterObject @ModelAttribute DashboardFilterRequest filter,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy dữ liệu top đối tác thành công", dashboardUseCase.getTopPartners(filter, limit))
        );
    }

    /** Xu hướng đăng ký theo ngày — dùng cho biểu đồ line chart */
    @GetMapping("/registration-trend")
    public ResponseEntity<ApiResponse<DashboardTrendResponse>> getRegistrationTrend(
            @Valid @ParameterObject @ModelAttribute DashboardFilterRequest filter) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy dữ liệu xu hướng đăng ký thành công", dashboardUseCase.getRegistrationTrend(filter))
        );
    }

    /** Doanh thu tổng và chi tiết theo bảng thi (A/B) — phục vụ BarChart Recharts */
    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<DashboardRevenueResponse>> getRevenue(
            @Valid @ParameterObject @ModelAttribute DashboardFilterRequest filter) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy dữ liệu doanh thu thành công", dashboardUseCase.getRevenue(filter))
        );
    }
}
