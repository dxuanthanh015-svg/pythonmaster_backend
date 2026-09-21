package com.thanhhandsome.pythonmaster.service.dashboard;

import com.thanhhandsome.pythonmaster.dto.request.dashboard.DashboardFilterRequest;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardDemographicsResponse;
import com.thanhhandsome.pythonmaster.repository.ThanhToanRepository;
import com.thanhhandsome.pythonmaster.repository.ThiSinhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardDemographicsService {
    private final ThanhToanRepository thanhToanRepository;
    private final ThiSinhRepository thiSinhRepository;

    private static final Set<String> NORTH_PROVINCES = Set.of(
            "hà nội", "hải phòng", "quảng ninh", "vĩnh phúc", "bắc ninh", "bắc giang", "hải dương",
            "hưng yên", "nam định", "ninh bình", "thái bình", "phú thọ", "thái nguyên", "lạng sơn",
            "cao bằng", "tuyên quang", "hà giang", "lào cai", "yên bái", "bắc kạn", "hòa bình",
            "sơn la", "điện biên", "lai châu", "hà nam"
    );

    private static final Set<String> SOUTH_PROVINCES = Set.of(
            "hồ chí minh", "tp. hồ chí minh", "tp hcm", "tphcm", "bình dương", "đồng nai",
            "vũng tàu", "bà rịa", "tây ninh", "bình phước", "long an", "tiền giang", "bến tre",
            "trà vinh", "vĩnh long", "đồng tháp", "an giang", "kiên giang", "cần thơ", "hậu giang",
            "sóc trăng", "bạc liêu", "cà mau"
    );

    public DashboardDemographicsResponse getDemographics(DashboardFilterRequest filter) {
        LocalDateTime fromDateTime = filter.getFrom() != null ? filter.getFrom().atStartOfDay() : null;
        LocalDateTime toExclusive = filter.getTo() != null ? filter.getTo().plusDays(1).atStartOfDay() : null;
        Long contestId = filter.getContestId();
        Long saleId = filter.getSaleId();

        // Education levels based on bangDau from ThanhToan
        List<Object[]> bangDauCounts = thanhToanRepository.countByBangDau(contestId, fromDateTime, toExclusive, saleId);
        long totalBangDauCount = 0;
        long countA = 0;
        long countB = 0;

        for (Object[] row : bangDauCounts) {
            String bang = row[0] != null ? row[0].toString() : "";
            long count = ((Number) row[1]).longValue();
            totalBangDauCount += count;
            if (bang.equalsIgnoreCase("Bảng A") || bang.toLowerCase().contains("a")) {
                countA += count;
            } else if (bang.equalsIgnoreCase("Bảng B") || bang.toLowerCase().contains("b")) {
                countB += count;
            }
        }

        List<DashboardDemographicsResponse.EducationLevelItem> educationLevels = List.of(
                DashboardDemographicsResponse.EducationLevelItem.builder()
                        .code("TABLE_A")
                        .name("Bảng A (Đội tuyển: 12 - 18 tuổi)")
                        .count(countA)
                        .percentage(calculatePercentage(countA, totalBangDauCount))
                        .build(),
                DashboardDemographicsResponse.EducationLevelItem.builder()
                        .code("TABLE_B")
                        .name("Bảng B (Lớp đại trà: 10 - 15 tuổi)")
                        .count(countB)
                        .percentage(calculatePercentage(countB, totalBangDauCount))
                        .build()
        );

        // Regions from ThiSinh
        List<Object[]> regionRows = thiSinhRepository.countByTinhThanh(fromDateTime, toExclusive, saleId);
        long totalRegionCount = 0;
        long north = 0;
        long south = 0;
        long central = 0;

        for (Object[] row : regionRows) {
            String province = row[0] != null ? row[0].toString().toLowerCase().trim() : "";
            long count = ((Number) row[1]).longValue();
            totalRegionCount += count;

            if (isNorth(province)) {
                north += count;
            } else if (isSouth(province)) {
                south += count;
            } else {
                central += count;
            }
        }

        List<DashboardDemographicsResponse.RegionItem> regions = List.of(
                DashboardDemographicsResponse.RegionItem.builder()
                        .regionName("Miền Bắc")
                        .count(north)
                        .percentage(calculatePercentage(north, totalRegionCount))
                        .build(),
                DashboardDemographicsResponse.RegionItem.builder()
                        .regionName("Miền Nam")
                        .count(south)
                        .percentage(calculatePercentage(south, totalRegionCount))
                        .build(),
                DashboardDemographicsResponse.RegionItem.builder()
                        .regionName("Miền Trung")
                        .count(central)
                        .percentage(calculatePercentage(central, totalRegionCount))
                        .build()
        );

        return DashboardDemographicsResponse.builder()
                .educationLevels(educationLevels)
                .regions(regions)
                .build();
    }

    private boolean isNorth(String province) {
        return NORTH_PROVINCES.stream().anyMatch(province::contains);
    }

    private boolean isSouth(String province) {
        return SOUTH_PROVINCES.stream().anyMatch(province::contains);
    }

    private BigDecimal calculatePercentage(long numerator, long denominator) {
        if (denominator == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(numerator)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(denominator), 1, RoundingMode.HALF_UP);
    }
}
