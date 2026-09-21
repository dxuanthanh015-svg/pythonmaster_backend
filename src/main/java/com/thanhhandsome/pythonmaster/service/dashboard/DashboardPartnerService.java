package com.thanhhandsome.pythonmaster.service.dashboard;

import com.thanhhandsome.pythonmaster.dto.request.dashboard.DashboardFilterRequest;
import com.thanhhandsome.pythonmaster.dto.response.dashboard.DashboardTopPartnersResponse;
import com.thanhhandsome.pythonmaster.entity.DoanhNghiep;
import com.thanhhandsome.pythonmaster.repository.DangKyWebRepository;
import com.thanhhandsome.pythonmaster.repository.DoanhNghiepRepository;
import com.thanhhandsome.pythonmaster.repository.ThiSinhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardPartnerService {
    private final ThiSinhRepository thiSinhRepository;
    private final DoanhNghiepRepository doanhNghiepRepository;
    private final DangKyWebRepository dangKyWebRepository;

    public DashboardTopPartnersResponse getTopPartners(DashboardFilterRequest filter, Integer limit) {
        int maxLimit = (limit != null && limit > 0) ? limit : 10;

        Map<String, Long> countByPartner = new HashMap<>();
        for (Object[] row : dangKyWebRepository.countThiSinhByPartner()) {
            if (row[0] != null) {
                countByPartner.put(row[0].toString().trim().toLowerCase(), ((Number) row[1]).longValue());
            }
        }
        // Fallback: thí sinh.truongHoc trùng tên đối tác (Excel từng ghi partner vào trường học)
        if (countByPartner.isEmpty()) {
            LocalDateTime fromDateTime = filter.getFrom() != null ? filter.getFrom().atStartOfDay() : null;
            LocalDateTime toExclusive = filter.getTo() != null ? filter.getTo().plusDays(1).atStartOfDay() : null;
            for (Object[] row : thiSinhRepository.countByTruongHoc(fromDateTime, toExclusive, filter.getSaleId())) {
                if (row[0] != null) {
                    countByPartner.put(row[0].toString().trim().toLowerCase(), ((Number) row[1]).longValue());
                }
            }
        }

        List<DoanhNghiep> enterprises = doanhNghiepRepository.findAll();
        List<DashboardTopPartnersResponse.PartnerItem> partnerItems = new ArrayList<>();
        long totalTopCandidates = 0;

        for (DoanhNghiep dn : enterprises) {
            long candidateCount = countByPartner.getOrDefault(
                    dn.getTenDoanhNghiep() != null ? dn.getTenDoanhNghiep().trim().toLowerCase() : "",
                    0L
            );
            partnerItems.add(DashboardTopPartnersResponse.PartnerItem.builder()
                    .id(dn.getId())
                    .name(dn.getTenDoanhNghiep())
                    .candidateCount(candidateCount)
                    .status("ACTIVE")
                    .build());
            totalTopCandidates += candidateCount;
        }

        partnerItems.sort(Comparator.comparingLong(DashboardTopPartnersResponse.PartnerItem::getCandidateCount).reversed());
        if (partnerItems.size() > maxLimit) {
            partnerItems = new ArrayList<>(partnerItems.subList(0, maxLimit));
            totalTopCandidates = partnerItems.stream().mapToLong(DashboardTopPartnersResponse.PartnerItem::getCandidateCount).sum();
        }

        return DashboardTopPartnersResponse.builder()
                .totalCandidatesInTop(totalTopCandidates)
                .partners(partnerItems)
                .build();
    }
}
