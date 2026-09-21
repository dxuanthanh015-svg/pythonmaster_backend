package com.thanhhandsome.pythonmaster.dto.request.doitac;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateDoiTacRequest {

    String tenDoanhNghiep;

    /** Email liên hệ — để trống nếu không có */
    String email;

    String nguoiDaiDien;
    String soDienThoai;
    String diaChi;

    /** Phân loại: "Đối tác", "Doanh nghiệp", "Tài trợ", v.v. — ánh xạ vào nganhNghe */
    String nganhNghe;
}
