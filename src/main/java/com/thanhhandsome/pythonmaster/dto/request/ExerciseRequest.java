package com.thanhhandsome.pythonmaster.dto.request;

import jakarta.validation.constraints.DecimalMax;
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
public class ExerciseRequest {
    @NotNull
    @Positive
    Long thiSinhId;

    @NotNull
    @Positive
    Long cuocThiId;

    @Positive
    Long nhanVienId;

    @NotBlank @Size(max = 255)
    String tenBaiTap;

    LocalDateTime hanNop;

    @NotBlank
    @Pattern(regexp = "(?i)created|submitted|graded", message = "Trạng thái phải là created, submitted hoặc graded")
    String trangThai;

    @DecimalMin(value = "0.00")
    @DecimalMax(value = "99.99", message = "Điểm hiện tối đa 99.99 theo DECIMAL(4,2)")
    BigDecimal diemSo;
}
