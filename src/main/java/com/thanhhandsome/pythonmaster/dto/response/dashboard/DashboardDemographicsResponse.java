package com.thanhhandsome.pythonmaster.dto.response.dashboard;

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
public class DashboardDemographicsResponse {
    List<EducationLevelItem> educationLevels;
    List<RegionItem> regions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class EducationLevelItem {
        String code;
        String name;
        long count;
        BigDecimal percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RegionItem {
        String regionName;
        long count;
        BigDecimal percentage;
    }
}
