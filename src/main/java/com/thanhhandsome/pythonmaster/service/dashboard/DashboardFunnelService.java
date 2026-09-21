package com.thanhhandsome.pythonmaster.service.dashboard;

import com.thanhhandsome.pythonmaster.dto.request.dashboard.DashboardFilterRequest;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardFunnelResponse;
import com.thanhhandsome.pythonmaster.repository.DangKyRepository;
import com.thanhhandsome.pythonmaster.repository.ThanhToanRepository;
import com.thanhhandsome.pythonmaster.repository.ThiSinhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardFunnelService {
    private final ThiSinhRepository thiSinhRepository;
    private final DangKyRepository dangKyRepository;
    private final ThanhToanRepository thanhToanRepository;

    public DashboardFunnelResponse getConversionFunnel(DashboardFilterRequest filter) {
        LocalDateTime fromDateTime = filter.getFrom() != null ? filter.getFrom().atStartOfDay() : null;
        LocalDateTime toExclusive = filter.getTo() != null ? filter.getTo().plusDays(1).atStartOfDay() : null;
        Long contestId = filter.getContestId();
        Long saleId = filter.getSaleId();

        long stage1Reach = thiSinhRepository.countDashboardLeads(fromDateTime, toExclusive, saleId);
        long stage2Reg = dangKyRepository.countDashboardRegistrations(contestId, fromDateTime, toExclusive, saleId);
        long stage3Paid = thanhToanRepository.countDashboardPaidRegistrations(contestId, fromDateTime, toExclusive, saleId);

        List<DashboardFunnelResponse.FunnelStageItem> stages = List.of(
                DashboardFunnelResponse.FunnelStageItem.builder()
                        .stage(1)
                        .name("Tiếp cận")
                        .count(stage1Reach)
                        .percentage(BigDecimal.valueOf(100.0))
                        .build(),
                DashboardFunnelResponse.FunnelStageItem.builder()
                        .stage(2)
                        .name("Đăng ký")
                        .count(stage2Reg)
                        .percentage(calculatePercentage(stage2Reg, stage1Reach))
                        .build(),
                DashboardFunnelResponse.FunnelStageItem.builder()
                        .stage(3)
                        .name("Tham gia")
                        .count(stage3Paid)
                        .percentage(calculatePercentage(stage3Paid, stage1Reach))
                        .build()
        );

        return DashboardFunnelResponse.builder()
                .stages(stages)
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
}
