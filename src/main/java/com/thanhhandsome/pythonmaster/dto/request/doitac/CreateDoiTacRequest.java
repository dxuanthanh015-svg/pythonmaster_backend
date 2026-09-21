package com.thanhhandsome.pythonmaster.dto.request.doitac;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateDoiTacRequest {

    @NotBlank(message = "Tên đơn vị không được để trống")
    String tenDoanhNghiep;

    @NotBlank(message = "Mã số thuế không được để trống")
    String maSoThue;

    String nguoiDaiDien;

    /** Email liên hệ — để trống nếu không có */
    String email;

    String soDienThoai;

    String diaChi;

    /** Phân loại: "Đối tác", "Doanh nghiệp", "Tài trợ", v.v. — ánh xạ vào nganhNghe */
    String nganhNghe;
}
