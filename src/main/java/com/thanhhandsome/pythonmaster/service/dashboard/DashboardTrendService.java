package com.thanhhandsome.pythonmaster.service.dashboard;

import com.thanhhandsome.pythonmaster.dto.request.dashboard.DashboardFilterRequest;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardTrendResponse;
import com.thanhhandsome.pythonmaster.repository.DangKyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardTrendService {
    private final DangKyRepository dangKyRepository;

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM");

    public DashboardTrendResponse getRegistrationTrend(DashboardFilterRequest filter) {
        LocalDateTime fromDateTime = filter.getFrom() != null ? filter.getFrom().atStartOfDay() : null;
        LocalDateTime toExclusive = filter.getTo() != null ? filter.getTo().plusDays(1).atStartOfDay() : null;
        Long contestId = filter.getContestId();
        Long saleId = filter.getSaleId();

        List<Object[]> rows = dangKyRepository.countDailyRegistrations(contestId, fromDateTime, toExclusive, saleId);

        // Xây map date -> count từ DB
        Map<LocalDate, Long> dateCountMap = new LinkedHashMap<>();
        for (Object[] row : rows) {
            LocalDate date = toLocalDate(row[0]);
            long count = ((Number) row[1]).longValue();
            if (date != null) {
                dateCountMap.put(date, count);
            }
        }

        // Tính tổng số ngày trong khoảng
        int totalDays = 0;
        List<DashboardTrendResponse.DailyDataPoint> dataPoints = new ArrayList<>();

        if (fromDateTime != null && toExclusive != null) {
            LocalDate cursor = fromDateTime.toLocalDate();
            LocalDate endDate = toExclusive.toLocalDate();
            while (!cursor.isAfter(endDate)) {
                long count = dateCountMap.getOrDefault(cursor, 0L);
                dataPoints.add(DashboardTrendResponse.DailyDataPoint.builder()
                        .date(cursor.format(DISPLAY_FORMAT))
                        .registrationCount(count)
                        .build());
                cursor = cursor.plusDays(1);
                totalDays++;
            }
        } else {
            // Không có filter ngày → trả về toàn bộ data từ DB
            for (Map.Entry<LocalDate, Long> entry : dateCountMap.entrySet()) {
                dataPoints.add(DashboardTrendResponse.DailyDataPoint.builder()
                        .date(entry.getKey().format(DISPLAY_FORMAT))
                        .registrationCount(entry.getValue())
                        .build());
                totalDays++;
            }
        }

        return DashboardTrendResponse.builder()
                .totalDays(totalDays)
                .data(dataPoints)
                .build();
    }

    private LocalDate toLocalDate(Object dateObj) {
        if (dateObj == null) return null;
        if (dateObj instanceof LocalDate ld) return ld;
        if (dateObj instanceof java.sql.Date sd) return sd.toLocalDate();
        // Hibernate có thể trả về LocalDate hoặc java.sql.Date
        return null;
    }
}
