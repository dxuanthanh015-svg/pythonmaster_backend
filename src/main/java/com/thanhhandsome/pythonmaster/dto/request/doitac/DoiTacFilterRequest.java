package com.thanhhandsome.pythonmaster.dto.request.doitac;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoiTacFilterRequest {

    /** Tìm theo tên, người liên hệ, email, SĐT, MST */
    String keyword;

    /** Lọc theo phân loại (nganhNghe): "Đối tác", "Doanh nghiệp", v.v. — null/blank = tất cả */
    String phanLoai;

    Integer page;
    Integer size;
    String  sortBy;
    String  sortDir;
}

