package com.thanhhandsome.pythonmaster.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CandidateRequest {
    @NotBlank
    @Size(max = 20)
    String idThiSinh;

    @Positive
    Long assignedSaleId;

    @NotBlank
    @Size(max = 100)
    String hoTen;

    LocalDate ngaySinh;

    @Email
    @Size(max = 255)
    String email;

    @NotBlank
    @Pattern(regexp = "^[0-9+() .-]{8,15}$", message = "Số điện thoại phải dài 8-15 ký tự hợp lệ")
    String soDienThoai;

    @Size(max = 20)
    String cccd;

    @Size(max = 2000)
    String diaChi;

    @Size(max = 100)
    String tinhThanh;

    @Size(max = 150)
    String truongHoc;
}
