package com.thanhhandsome.pythonmaster.dto.response.dashboard;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardTopPartnersResponse {
    long totalCandidatesInTop;
    List<PartnerItem> partners;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PartnerItem {
        Long id;
        String name;
        long candidateCount;
        String status;
    }
}
