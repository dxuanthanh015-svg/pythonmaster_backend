package com.thanhhandsome.pythonmaster.service.dashboard;

import com.thanhhandsome.pythonmaster.dto.request.dashboard.DashboardFilterRequest;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardRevenueResponse;
import com.thanhhandsome.pythonmaster.repository.ThanhToanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardRevenueService {
    private final ThanhToanRepository thanhToanRepository;

    public DashboardRevenueResponse getRevenue(DashboardFilterRequest filter) {
        LocalDateTime fromDateTime = filter.getFrom() != null ? filter.getFrom().atStartOfDay() : null;
        LocalDateTime toExclusive = filter.getTo() != null ? filter.getTo().plusDays(1).atStartOfDay() : null;
        Long contestId = filter.getContestId();
        Long saleId = filter.getSaleId();

        List<Object[]> rows = thanhToanRepository.sumRevenueByBangDau(contestId, fromDateTime, toExclusive, saleId);

        BigDecimal tongDoanhThu = BigDecimal.ZERO;
        BigDecimal revA = BigDecimal.ZERO;
        BigDecimal revB = BigDecimal.ZERO;

        for (Object[] row : rows) {
            String bang = row[0] != null ? row[0].toString() : "";
            BigDecimal amount = BigDecimal.ZERO;
            if (row[1] instanceof BigDecimal bd) {
                amount = bd;
            } else if (row[1] instanceof Number num) {
                amount = BigDecimal.valueOf(num.doubleValue());
            }

            tongDoanhThu = tongDoanhThu.add(amount);

            if (bang.equalsIgnoreCase("Bảng A") || bang.toLowerCase().contains("a")) {
                revA = revA.add(amount);
            } else if (bang.equalsIgnoreCase("Bảng B") || bang.toLowerCase().contains("b")) {
                revB = revB.add(amount);
            }
        }

        List<DashboardRevenueResponse.BangRevenueItem> chiTiet = new ArrayList<>();
        chiTiet.add(DashboardRevenueResponse.BangRevenueItem.builder()
                .name("Bảng A")
                .doanhThu(revA)
                .build());
        chiTiet.add(DashboardRevenueResponse.BangRevenueItem.builder()
                .name("Bảng B")
                .doanhThu(revB)
                .build());

        return DashboardRevenueResponse.builder()
                .tongDoanhThu(tongDoanhThu)
                .chiTietTheoBang(chiTiet)
                .build();
    }
}
