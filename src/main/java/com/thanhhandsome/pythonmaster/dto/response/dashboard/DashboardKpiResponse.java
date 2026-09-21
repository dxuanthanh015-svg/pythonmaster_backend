package com.thanhhandsome.pythonmaster.dto.response.dashboard;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardKpiResponse {
    /** Tổng lượt đăng ký + tăng trưởng so với kỳ trước */
    RegistrationKpi registration;

    /** Tỷ lệ chuyển đổi thanh toán (paid / registration) + delta */
    ConversionKpi conversion;

    /** Doanh thu tạm tính (VND) + tăng trưởng */
    RevenueKpi revenue;

    /** Lead cần chăm sóc lại (chưa hoàn tất thanh toán) */
    LeadFollowUpKpi leadFollowUp;

    /** Target Gap = mục tiêu kỳ - thực tế */
    TargetGapKpi targetGap;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RegistrationKpi {
        /** Tổng lượt đăng ký trong kỳ */
        long count;
        /** % tăng trưởng so với kỳ trước (dương = tăng, âm = giảm) */
        BigDecimal growthRate;
        /** Nhãn kỳ so sánh, ví dụ: "so với kỳ trước" */
        String growthLabel;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ConversionKpi {
        /** Tỷ lệ chuyển đổi thanh toán = paid / registration * 100 */
        BigDecimal rate;
        /** % delta so với kỳ trước */
        BigDecimal deltaRate;
        /** Mô tả, ví dụ: "Trên tổng số đăng ký" */
        String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RevenueKpi {
        /** Doanh thu tạm tính */
        BigDecimal amount;
        /** Đơn vị tiền tệ */
        String currency;
        /** % tăng trưởng so với kỳ trước */
        BigDecimal growthRate;
        /** Mô tả, ví dụ: "Đã quy đổi VND" */
        String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class LeadFollowUpKpi {
        /** Số lead đã đăng ký nhưng chưa hoàn tất thanh toán */
        long count;
        /** % tăng trưởng so với kỳ trước */
        BigDecimal growthRate;
        /** Mô tả, ví dụ: "Chưa hoàn tất thanh toán" */
        String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TargetGapKpi {
        /** Khoảng cách so với mục tiêu (âm = dưới target) */
        long gap;
        /** % so với mục tiêu */
        BigDecimal gapRate;
        /** Mô tả, ví dụ: "So với mục tiêu quý" */
        String description;
    }
}
