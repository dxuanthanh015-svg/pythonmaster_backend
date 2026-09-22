package com.thanhhandsome.pythonmaster.dto.request.analytics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalesAnalyticsFilterRequest {
    @Positive Long contestId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to;
    @Positive Long saleId;

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "to phải lớn hơn hoặc bằng from")
    public boolean isValidDateRange() {
        return from == null || to == null || !to.isBefore(from);
    }
}
