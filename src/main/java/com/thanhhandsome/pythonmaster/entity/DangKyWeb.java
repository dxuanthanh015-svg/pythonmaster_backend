package com.thanhhandsome.pythonmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "DANGKYWEB")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DangKyWeb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_THI_SINH")
    ThiSinh thiSinh;

    @Column(name = "THOI_GIAN_THANH_TOAN")
    LocalDateTime thoiGianThanhToan;

    @Column(name = "BANG_DAU", nullable = false, length = 20)
    String bangDau;

    @Column(name = "HO_TEN", columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String hoTen;

    @Column(name = "SO_DIEN_THOAI", length = 15)
    String soDienThoai;

    @Column(name = "EMAIL")
    String email;

    @Column(name = "TINH_THANH", columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String tinhThanh;

    @Column(name = "TRUONG_HOC", columnDefinition = "VARCHAR(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String truongHoc;

    @Column(name = "SO_TIEN")
    Double soTien;

    @Column(name = "_PARTNER", columnDefinition = "VARCHAR(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String partner;

    @Builder.Default
    @Column(name = "TRANG_THAI_THANH_TOAN", nullable = false, length = 20)
    String trangThaiThanhToan = "pending";

    @Column(name = "ID_GIAO_DICH", length = 100)
    String idGiaoDich;

    @Column(name = "GHI_CHU", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String ghiChu;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    LocalDateTime createdAt;
}