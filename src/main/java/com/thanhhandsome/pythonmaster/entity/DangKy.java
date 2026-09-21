package com.thanhhandsome.pythonmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "DANGKY", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_THISINH_CUOCTHI", columnNames = {"ID_THI_SINH", "ID_CUOC_THI"})
})
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DangKy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_THI_SINH", nullable = false)
    ThiSinh thiSinh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CUOC_THI", nullable = false)
    CuocThi cuocThi;

    @Column(name = "NGAY_DANG_KY")
    LocalDateTime ngayDangKy;

    @Builder.Default
    @Column(name = "TRANG_THAI_DANG_KY", nullable = false, length = 20)
    String trangThaiDangKy = "pending";

    @OneToOne(mappedBy = "dangKy", fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    ThanhToan thanhToan;

    @PrePersist
    protected void onCreate() {
        if (ngayDangKy == null) {
            ngayDangKy = LocalDateTime.now();
        }
    }
}
