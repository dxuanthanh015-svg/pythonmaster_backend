package com.thanhhandsome.pythonmaster.dto.response.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardRevenueResponse {

    @JsonProperty("tong_doanh_thu")
    BigDecimal tongDoanhThu;

    @JsonProperty("chi_tiet_theo_bang")
    List<BangRevenueItem> chiTietTheoBang;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BangRevenueItem {
        String name;

        @JsonProperty("doanh_thu")
        BigDecimal doanhThu;
    }
}
