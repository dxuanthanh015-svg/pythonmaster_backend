package com.thanhhandsome.pythonmaster.dto.response.doitac;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoiTacResponse {

    Long id;

    /** Tên đơn vị */
    String tenDoanhNghiep;

    /** Mã số thuế */
    String maSoThue;

    /** Người liên hệ */
    String nguoiDaiDien;

    /** Email liên hệ */
    String email;

    /** Số điện thoại */
    String soDienThoai;

    /** Địa chỉ */
    String diaChi;

    /** Phân loại đối tác: "Đối tác", "Doanh nghiệp", "Tài trợ", v.v. */
    String phanLoai;

    /** Nhân viên phụ trách */
    String nhanVienPhuTrach;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
