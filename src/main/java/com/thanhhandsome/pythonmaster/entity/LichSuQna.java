package com.thanhhandsome.pythonmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "LICHSUQNA")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LichSuQna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "ID_QA", length = 50)
    String idQa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_THI_SINH")
    ThiSinh thiSinh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DOANH_NGHIEP")
    DoanhNghiep doanhNghiep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TAI_LIEU")
    TaiLieu taiLieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_NHAN_VIEN")
    NhanVien nhanVien;

    @Column(name = "NOI_DUNG_HOI", nullable = false, columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String noiDungHoi;

    @Column(name = "NOI_DUNG_TRA_LOI", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String noiDungTraLoi;

    @Column(name = "THOI_GIAN")
    LocalDateTime thoiGian;

    @Column(name = "KENH_TUONG_TAC", columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String kenhTuongTac;

    @Builder.Default
    @Column(name = "TRANG_THAI", nullable = false, length = 20)
    String trangThai = "unanswered";

    @Column(name = "DANH_GIA_HAI_LONG")
    Integer danhGiaHaiLong;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    LocalDateTime updatedAt;
}