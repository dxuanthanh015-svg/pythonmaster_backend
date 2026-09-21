package com.thanhhandsome.pythonmaster.service.dashboard;

import com.thanhhandsome.pythonmaster.dto.request.dashboard.DashboardFilterRequest;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardKpiResponse;
import com.thanhhandsome.pythonmaster.repository.DangKyRepository;
import com.thanhhandsome.pythonmaster.repository.ThanhToanRepository;
import com.thanhhandsome.pythonmaster.repository.ThiSinhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardKpiService {
    private final DangKyRepository dangKyRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final ThiSinhRepository thiSinhRepository;

    public DashboardKpiResponse getKpis(DashboardFilterRequest filter) {
        LocalDateTime fromDateTime = filter.getFrom() != null ? filter.getFrom().atStartOfDay() : null;
        LocalDateTime toExclusive = filter.getTo() != null ? filter.getTo().plusDays(1).atStartOfDay() : null;
        Long contestId = filter.getContestId();
        Long saleId = filter.getSaleId();

        // --- Kỳ hiện tại ---
        long regCount = dangKyRepository.countDashboardRegistrations(contestId, fromDateTime, toExclusive, saleId);
        long paidCount = thanhToanRepository.countDashboardPaidRegistrations(contestId, fromDateTime, toExclusive, saleId);
        BigDecimal revenueAmount = thanhToanRepository.sumDashboardPaidRevenue(contestId, fromDateTime, toExclusive, saleId);
        long leadFollowUp = dangKyRepository.countLeadNeedFollowUp(contestId, fromDateTime, toExclusive, saleId);

        // --- Kỳ trước (so sánh tăng trưởng) ---
        long prevRegCount = 0;
        long prevPaidCount = 0;
        BigDecimal prevRevenue = BigDecimal.ZERO;
        long prevLeadFollowUp = 0;

        if (fromDateTime != null && toExclusive != null) {
            long periodDays = java.time.temporal.ChronoUnit.DAYS.between(fromDateTime, toExclusive);
            LocalDateTime prevFrom = fromDateTime.minusDays(periodDays);
            LocalDateTime prevTo = fromDateTime;
            prevRegCount = dangKyRepository.countDashboardRegistrations(contestId, prevFrom, prevTo, saleId);
            prevPaidCount = thanhToanRepository.countDashboardPaidRegistrations(contestId, prevFrom, prevTo, saleId);
            BigDecimal prev = thanhToanRepository.sumDashboardPaidRevenue(contestId, prevFrom, prevTo, saleId);
            prevRevenue = prev != null ? prev : BigDecimal.ZERO;
            prevLeadFollowUp = dangKyRepository.countLeadNeedFollowUp(contestId, prevFrom, prevTo, saleId);
        }

        // --- Tính toán ---
        BigDecimal regGrowthRate = calculateGrowthRate(regCount, prevRegCount);
        BigDecimal conversionRate = calculatePercentage(paidCount, regCount);
        BigDecimal prevConversionRate = calculatePercentage(prevPaidCount, prevRegCount);
        BigDecimal conversionDelta = conversionRate.subtract(prevConversionRate).setScale(1, RoundingMode.HALF_UP);
        BigDecimal revenueAmount_ = revenueAmount != null ? revenueAmount : BigDecimal.ZERO;
        BigDecimal revenueGrowthRate = calculateGrowthRate(revenueAmount_.longValue(), prevRevenue.longValue());
        BigDecimal leadFollowUpGrowthRate = calculateGrowthRate(leadFollowUp, prevLeadFollowUp);

        // --- Target Gap: mục tiêu kỳ cố định (có thể cấu hình sau) ---
        // Tạm thời dùng prevRegCount * 1.1 làm target
        long target = (long) (prevRegCount * 1.1);
        long gap = regCount - target;
        BigDecimal gapRate = target > 0
                ? BigDecimal.valueOf(gap).multiply(BigDecimal.valueOf(100))
                  .divide(BigDecimal.valueOf(target), 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return DashboardKpiResponse.builder()
                .registration(DashboardKpiResponse.RegistrationKpi.builder()
                        .count(regCount)
                        .growthRate(regGrowthRate)
                        .growthLabel("So với kỳ trước")
                        .build())
                .conversion(DashboardKpiResponse.ConversionKpi.builder()
                        .rate(conversionRate)
                        .deltaRate(conversionDelta)
                        .description("Trên tổng số đăng ký")
                        .build())
                .revenue(DashboardKpiResponse.RevenueKpi.builder()
                        .amount(revenueAmount_)
                        .currency("VND")
                        .growthRate(revenueGrowthRate)
                        .description("Đã quy đổi VND")
                        .build())
                .leadFollowUp(DashboardKpiResponse.LeadFollowUpKpi.builder()
                        .count(leadFollowUp)
                        .growthRate(leadFollowUpGrowthRate)
                        .description("Chưa hoàn tất thanh toán")
                        .build())
                .targetGap(DashboardKpiResponse.TargetGapKpi.builder()
                        .gap(gap)
                        .gapRate(gapRate)
                        .description("So với mục tiêu quý")
                        .build())
                .build();
    }

    private BigDecimal calculatePercentage(long numerator, long denominator) {
        if (denominator == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(numerator)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(denominator), 1, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateGrowthRate(long current, long previous) {
        if (previous == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(current - previous)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(previous), 1, RoundingMode.HALF_UP);
    }
}
