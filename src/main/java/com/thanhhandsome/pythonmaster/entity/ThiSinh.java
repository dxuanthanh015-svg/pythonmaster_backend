package com.thanhhandsome.pythonmaster.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "THISINH")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThiSinh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "ID_THI_SINH", nullable = false, unique = true, length = 20)
    String idThiSinh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNED_SALE_ID")
    NhanVien assignedSale;

    @Column(name = "HO_TEN", nullable = false, columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String hoTen;

    @Column(name = "NGAY_SINH")
    LocalDate ngaySinh;

    @Column(name = "EMAIL")
    String email;

    @Column(name = "SO_DIEN_THOAI", nullable = false, length = 15)
    String soDienThoai;

    @Column(name = "CCCD", length = 20)
    String cccd;

    @Column(name = "DIA_CHI", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String diaChi;

    @Column(name = "TINH_THANH", columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String tinhThanh;

    @Column(name = "TRUONG_HOC", columnDefinition = "VARCHAR(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String truongHoc;

    @Builder.Default
    @Column(name = "TRANG_THAI_HO_SO", nullable = false, length = 30)
    String trangThaiHoSo = "created";

    @Builder.Default @OneToMany(mappedBy = "thiSinh")
    @JsonIgnore @ToString.Exclude @EqualsAndHashCode.Exclude
    List<DangKy> dangKys = new ArrayList<>();

    @Builder.Default @OneToMany(mappedBy = "thiSinh")
    @JsonIgnore @ToString.Exclude @EqualsAndHashCode.Exclude
    List<BaiTap> baiTaps = new ArrayList<>();

    @Builder.Default @OneToMany(mappedBy = "thiSinh")
    @JsonIgnore @ToString.Exclude @EqualsAndHashCode.Exclude
    List<FbAds> fbAdsLogs = new ArrayList<>();

    @Builder.Default @OneToMany(mappedBy = "thiSinh")
    @JsonIgnore @ToString.Exclude @EqualsAndHashCode.Exclude
    List<GgForm> ggForms = new ArrayList<>();

    @Builder.Default @OneToMany(mappedBy = "thiSinh")
    @JsonIgnore @ToString.Exclude @EqualsAndHashCode.Exclude
    List<DangKyWeb> dangKyWebs = new ArrayList<>();

    @Builder.Default @OneToMany(mappedBy = "thiSinh")
    @JsonIgnore @ToString.Exclude @EqualsAndHashCode.Exclude
    List<LichSuQna> lichSuQnas = new ArrayList<>();

    @Column(name = "CREATED_AT")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
