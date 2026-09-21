package com.thanhhandsome.pythonmaster.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "NHANVIEN")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "ID_NHAN_VIEN", nullable = false, unique = true, length = 20)
    String idNhanVien;

    @Column(name = "HO_TEN", nullable = false, columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String hoTen;

    @Column(name = "EMAIL", nullable = false)
    String email;

    @Column(name = "SO_DIEN_THOAI", length = 15)
    String soDienThoai;

    @Column(name = "PHONG_BAN", columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String phongBan;

    @Builder.Default
    @Column(name = "PHAN_QUYEN", nullable = false, length = 20)
    String phanQuyen = "staff";

    @Builder.Default @OneToMany(mappedBy = "assignedSale")
    @JsonIgnore @ToString.Exclude @EqualsAndHashCode.Exclude
    List<ThiSinh> thiSinhsPhuTrach = new ArrayList<>();

    @Builder.Default @OneToMany(mappedBy = "nhanVien")
    @JsonIgnore @ToString.Exclude @EqualsAndHashCode.Exclude
    List<BaiTap> baiTapsCham = new ArrayList<>();

    @Builder.Default @OneToMany(mappedBy = "nhanVien")
    List<DoanhNghiep> doanhNghiepsPhuTrach = new ArrayList<>();

    @Builder.Default @OneToMany(mappedBy = "nhanVien")
    @JsonIgnore @ToString.Exclude @EqualsAndHashCode.Exclude
    List<LichSuQna> lichSuQnas = new ArrayList<>();

    @Builder.Default @OneToMany(mappedBy = "createdBy")
    @JsonIgnore @ToString.Exclude @EqualsAndHashCode.Exclude
    List<TaiLieu> taiLieuDaTao = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    LocalDateTime updatedAt;
}
