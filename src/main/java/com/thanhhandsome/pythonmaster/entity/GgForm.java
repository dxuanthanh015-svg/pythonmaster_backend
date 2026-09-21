package com.thanhhandsome.pythonmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "GGFORM")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GgForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FORM_LOG")
    Long formLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_THI_SINH")
    ThiSinh thiSinh;

    @CreationTimestamp
    @Column(name = "THOI_GIAN")
    LocalDateTime thoiGian;

    @Column(name = "HO_TEN", columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String hoTen;

    @Column(name = "NGAY_SINH")
    LocalDate ngaySinh;

    @Column(name = "SO_DIEN_THOAI", length = 15)
    String soDienThoai;

    @Column(name = "EMAIL")
    String email;

    @Column(name = "TRUONG_HOC", columnDefinition = "VARCHAR(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String truongHoc;

    @Column(name = "KENH_BIET", columnDefinition = "VARCHAR(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String kenhBiet;

    @Builder.Default
    @Column(name = "TRANG_THAI_GHI_DANH", nullable = false, length = 20)
    String trangThaiGhiDanh = "new";

    @Column(name = "MOI_QUAN_TAM", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String moiQuanTam;

    @Column(name = "CAU_HOI", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String cauHoi;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    LocalDateTime createdAt;
}