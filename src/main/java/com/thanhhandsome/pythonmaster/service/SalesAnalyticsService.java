package com.thanhhandsome.pythonmaster.service;

import com.thanhhandsome.pythonmaster.dto.response.analytics.FunnelResponse;
import com.thanhhandsome.pythonmaster.dto.response.analytics.PaymentKpiResponse;
import com.thanhhandsome.pythonmaster.dto.response.analytics.SaleKpiResponse;
import com.thanhhandsome.pythonmaster.dto.response.analytics.SalesAnalyticsResponse;
import com.thanhhandsome.pythonmaster.entity.NhanVien;
import com.thanhhandsome.pythonmaster.mapper.SalesAnalyticsMapper;
import com.thanhhandsome.pythonmaster.repository.BaiTapRepository;
import com.thanhhandsome.pythonmaster.repository.DangKyRepository;
import com.thanhhandsome.pythonmaster.repository.FbAdsRepository;
import com.thanhhandsome.pythonmaster.repository.GgFormRepository;
import com.thanhhandsome.pythonmaster.repository.NhanVienRepository;
import com.thanhhandsome.pythonmaster.repository.ThanhToanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SalesAnalyticsService implements SalesAnalyticsUseCase {
    private final FbAdsRepository fbAdsRepository;
    private final GgFormRepository ggFormRepository;
    private final BaiTapRepository baiTapRepository;
    private final DangKyRepository dangKyRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final SalesAnalyticsMapper salesAnalyticsMapper;

    public SalesAnalyticsService(FbAdsRepository fbAdsRepository,
                                 GgFormRepository ggFormRepository,
                                 BaiTapRepository baiTapRepository,
                                 DangKyRepository dangKyRepository,
                                 ThanhToanRepository thanhToanRepository,
                                 NhanVienRepository nhanVienRepository,
                                 SalesAnalyticsMapper salesAnalyticsMapper) {
        this.fbAdsRepository = fbAdsRepository;
        this.ggFormRepository = ggFormRepository;
        this.baiTapRepository = baiTapRepository;
        this.dangKyRepository = dangKyRepository;
        this.thanhToanRepository = thanhToanRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.salesAnalyticsMapper = salesAnalyticsMapper;
    }

    @Override
    public SalesAnalyticsResponse getAnalytics(Long contestId, LocalDate from, LocalDate to, Long saleId) {
        // Defaults khi không truyền filter
        LocalDate effectiveFrom = from != null ? from : LocalDate.of(2026, 1, 1);
        LocalDate effectiveTo   = to   != null ? to   : LocalDate.now();

        if (effectiveTo.isBefore(effectiveFrom)) {
            throw new IllegalArgumentException("to must be on or after from");
        }

        LocalDateTime fromDateTime = effectiveFrom.atStartOfDay();
        LocalDateTime toExclusive = effectiveTo.plusDays(1).atStartOfDay();
        FunnelResponse funnel = buildFunnel(contestId, fromDateTime, toExclusive, saleId);
        PaymentKpiResponse paymentKpi = buildPaymentKpi(contestId, fromDateTime, toExclusive, saleId, funnel.getRegistrations());

        List<NhanVien> sales = saleId == null
                ? nhanVienRepository.findByPhanQuyenIgnoreCase("sale")
                : nhanVienRepository.findById(saleId).stream().toList();

        List<SaleKpiResponse> kpis = sales.stream()
                .map(sale -> toSaleKpi(sale, contestId, fromDateTime, toExclusive))
                .toList();
        return new SalesAnalyticsResponse(contestId, effectiveFrom, effectiveTo, funnel, paymentKpi, kpis);
    }

    private SaleKpiResponse toSaleKpi(NhanVien sale, Long contestId, LocalDateTime from, LocalDateTime to) {
        FunnelResponse funnel = buildFunnel(contestId, from, to, sale.getId());
        PaymentKpiResponse payment = buildPaymentKpi(contestId, from, to, sale.getId(), funnel.getRegistrations());
        return salesAnalyticsMapper.toSaleKpi(sale, funnel, payment);
    }

    private FunnelResponse buildFunnel(Long contestId, LocalDateTime from, LocalDateTime to, Long saleId) {
        long facebookApproaches = fbAdsRepository.countApproaches(from, to, saleId);
        long googleFormApproaches = ggFormRepository.countApproaches(from, to, saleId);
        long totalApproaches = facebookApproaches + googleFormApproaches;
        long registrations = dangKyRepository.countRegistrations(contestId, from, to, saleId);
        long participants = baiTapRepository.countParticipants(contestId, from, to, saleId);
        return salesAnalyticsMapper.toFunnel(facebookApproaches, googleFormApproaches, registrations, participants,
                percentage(registrations, totalApproaches), percentage(participants, registrations),
                percentage(participants, totalApproaches));
    }

    private PaymentKpiResponse buildPaymentKpi(Long contestId, LocalDateTime from, LocalDateTime to,
                                               Long saleId, long registrations) {
        long paidRegistrations = thanhToanRepository.countPaidRegistrations(contestId, from, to, saleId);
        BigDecimal revenue = thanhToanRepository.sumPaidRevenue(contestId, from, to, saleId);
        return salesAnalyticsMapper.toPaymentKpi(
                paidRegistrations, revenue, percentage(paidRegistrations, registrations));
    }

    private BigDecimal percentage(long numerator, long denominator) {
        if (denominator == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(numerator)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP);
    }
}
