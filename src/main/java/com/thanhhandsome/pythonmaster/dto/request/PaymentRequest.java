package com.thanhhandsome.pythonmaster.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentRequest {
    @NotNull
    @Positive
    Long dangKyId;

    LocalDateTime thoiGianGiaoDich;

    @NotBlank
    @Size(max = 20)
    String bangDau;

    @NotNull
    @DecimalMin(value = "0.01") BigDecimal soTien;

    @NotBlank
    @Pattern(regexp = "(?i)pending|paid|failed", message = "Trạng thái phải là pending, paid hoặc failed") String paymentStatus;

    @Size(max = 100)
    String idGiaoDich;
}
