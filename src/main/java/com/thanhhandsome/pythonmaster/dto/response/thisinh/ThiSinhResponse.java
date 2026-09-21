package com.thanhhandsome.pythonmaster.dto.response.thisinh;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThiSinhResponse {
    Long id;
    String idThiSinh;
    String hoTen;
    String email;
    String soDienThoai;
    String cccd;
    String truongHoc;
    String tinhThanh;
    String diaChi;
    LocalDate ngaySinh;
    String trangThaiHoSo;

    // Các trường hiển thị bảng
    String trangThai;          // "Đã đóng phí" | "Chờ hồ sơ" | "Chưa đóng phí"
    String trangThaiKey;       // "DA_DONG_PHI" | "CHO_HO_SO" | "CHUA_DONG_PHI"
    String bangDau;            // "Bảng A" | "Bảng B"
    BigDecimal soTien;         // Số tiền lệ phí
    String paymentStatus;      // "completed" | "pending"
    LocalDateTime ngayDangKy;  // Thời gian đăng ký
    LocalDateTime createdAt;
}
