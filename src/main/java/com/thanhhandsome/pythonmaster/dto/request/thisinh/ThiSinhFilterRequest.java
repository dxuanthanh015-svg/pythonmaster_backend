package com.thanhhandsome.pythonmaster.dto.request.thisinh;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThiSinhFilterRequest {
    String keyword;     // Tìm kiếm họ tên, sđt, email, trường học
    String truongHoc;   // Tên trường hoặc "all"
    String trangThai;   // "ALL" | "DA_DONG_PHI" | "CHO_HO_SO" | "CHUA_DONG_PHI"

    @Builder.Default
    Integer page = 0;

    @Builder.Default
    Integer size = 10;

    @Builder.Default
    String sortBy = "id";

    @Builder.Default
    String sortDir = "asc";
}
