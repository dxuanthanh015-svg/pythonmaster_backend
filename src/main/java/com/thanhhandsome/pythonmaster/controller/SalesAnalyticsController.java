package com.thanhhandsome.pythonmaster.controller;

import com.thanhhandsome.pythonmaster.dto.request.analytics.SalesAnalyticsFilterRequest;
import com.thanhhandsome.pythonmaster.dto.response.ApiResponse;
import com.thanhhandsome.pythonmaster.dto.response.analytics.SalesAnalyticsResponse;
import com.thanhhandsome.pythonmaster.service.SalesAnalyticsUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics/sales")
@Tag(name = "Sales Analytics", description = "Phân tích bán hàng theo sale / contest")
public class SalesAnalyticsController {
    private final SalesAnalyticsUseCase salesAnalyticsUseCase;

    public SalesAnalyticsController(SalesAnalyticsUseCase salesAnalyticsUseCase) {
        this.salesAnalyticsUseCase = salesAnalyticsUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<SalesAnalyticsResponse>> getSalesAnalytics(
            @Valid @ModelAttribute SalesAnalyticsFilterRequest filter) {
        SalesAnalyticsResponse data = salesAnalyticsUseCase.getAnalytics(
                filter.getContestId(), filter.getFrom(), filter.getTo(), filter.getSaleId());
        return ResponseEntity.ok(ApiResponse.success("Lấy dữ liệu phân tích bán hàng thành công", data));
    }
}
