package com.thanhhandsome.pythonmaster.mapper;

import com.thanhhandsome.pythonmaster.dto.response.analytics.FunnelResponse;
import com.thanhhandsome.pythonmaster.dto.response.analytics.PaymentKpiResponse;
import com.thanhhandsome.pythonmaster.dto.response.analytics.SaleKpiResponse;
import com.thanhhandsome.pythonmaster.entity.NhanVien;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Component Mapper xử lý mapping dữ liệu Sales Analytics sang DTO.
 * Đánh dấu @Component để Spring tự động inject vào SalesAnalyticsService
 * mà không bị phụ thuộc vào annotation processor của IDE.
 */
@Component
public class SalesAnalyticsMapper {

    public FunnelResponse toFunnel(long facebookApproaches, long googleFormApproaches,
                                   long registrations, long participants,
                                   BigDecimal approachToRegistrationRate,
                                   BigDecimal registrationToParticipationRate,
                                   BigDecimal overallParticipationRate) {
        return FunnelResponse.builder()
                .facebookApproaches(facebookApproaches)
                .googleFormApproaches(googleFormApproaches)
                .registrations(registrations)
                .participants(participants)
                .approachToRegistrationRate(approachToRegistrationRate)
                .registrationToParticipationRate(registrationToParticipationRate)
                .overallParticipationRate(overallParticipationRate)
                .totalApproaches(facebookApproaches + googleFormApproaches)
                .deduplicated(false)
                .build();
    }

    public PaymentKpiResponse toPaymentKpi(long paidRegistrations, BigDecimal revenue,
                                           BigDecimal registrationToPaymentRate) {
        return PaymentKpiResponse.builder()
                .paidRegistrations(paidRegistrations)
                .revenue(revenue)
                .registrationToPaymentRate(registrationToPaymentRate)
                .build();
    }

    public SaleKpiResponse toSaleKpi(NhanVien sale, FunnelResponse funnel, PaymentKpiResponse paymentKpi) {
        SaleKpiResponse.SaleKpiResponseBuilder builder = SaleKpiResponse.builder();

        if (sale != null) {
            builder.saleId(sale.getId())
                   .saleCode(sale.getIdNhanVien())
                   .saleName(sale.getHoTen());
        }
        if (funnel != null) {
            builder.totalApproaches(funnel.getTotalApproaches())
                   .registrations(funnel.getRegistrations())
                   .participants(funnel.getParticipants())
                   .approachToRegistrationRate(funnel.getApproachToRegistrationRate())
                   .registrationToParticipationRate(funnel.getRegistrationToParticipationRate());
        }
        if (paymentKpi != null) {
            builder.paidRegistrations(paymentKpi.getPaidRegistrations())
                   .revenue(paymentKpi.getRevenue())
                   .registrationToPaymentRate(paymentKpi.getRegistrationToPaymentRate());
        }

        return builder.build();
    }
}
