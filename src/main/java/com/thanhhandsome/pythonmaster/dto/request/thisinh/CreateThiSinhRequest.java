package com.thanhhandsome.pythonmaster.dto.request.thisinh;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateThiSinhRequest {
    @NotBlank(message = "Họ và tên không được để trống")
    String hoTen;

    @Email(message = "Email không đúng định dạng")
    String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    String soDienThoai;

    String cccd;
    String truongHoc;
    String tinhThanh;
    String diaChi;
    LocalDate ngaySinh;

    @Builder.Default
    String bangDau = "Bảng A";

    @Builder.Default
    String trangThaiHoSo = "created";

    @Builder.Default
    String paymentStatus = "completed"; // "completed" | "pending"

    BigDecimal soTien;
}
