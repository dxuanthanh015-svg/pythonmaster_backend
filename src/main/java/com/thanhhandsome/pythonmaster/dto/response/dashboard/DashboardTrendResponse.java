package com.thanhhandsome.pythonmaster.dto.response.dashboard;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Response cho biểu đồ "Xu hướng đăng ký theo ngày".
 * Mỗi điểm là tổng lượt đăng ký mới trong một ngày.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardTrendResponse {
    /** Số ngày trong khoảng lọc (dùng để hiển thị tiêu đề chart) */
    int totalDays;
    /** Danh sách các điểm dữ liệu theo ngày */
    List<DailyDataPoint> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DailyDataPoint {
        /** Ngày, định dạng dd/MM (VD: "02/09") */
        String date;
        /** Tổng lượt đăng ký trong ngày */
        long registrationCount;
    }
}
