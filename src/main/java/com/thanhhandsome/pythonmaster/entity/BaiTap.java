package com.thanhhandsome.pythonmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "BAITAP")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaiTap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_THI_SINH", nullable = false)
    ThiSinh thiSinh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CUOC_THI", nullable = false)
    CuocThi cuocThi;

    @Column(name = "ID_TASK")
    Long idTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_NHAN_VIEN")
    NhanVien nhanVien;

    @Column(name = "TEN_BAI_TAP", nullable = false, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String tenBaiTap;

    @Column(name = "MO_TA", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String moTa;

    @Column(name = "HAN_NOP")
    LocalDateTime hanNop;

    @Builder.Default
    @Column(name = "TRANG_THAI", nullable = false, length = 20)
    String trangThai = "created";

    @Column(name = "FILE_NOP_PATH", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String fileNopPath;

    @Column(name = "DIEM_SO", precision = 4, scale = 2)
    BigDecimal diemSo;

    @Column(name = "NHAN_XET", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String nhanXet;

    @Column(name = "NGAY_NOP")
    LocalDateTime ngayNop;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    LocalDateTime createdAt;
}