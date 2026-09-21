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
@Table(name = "DOANHNGHIEP")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoanhNghiep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "TEN_DOANH_NGHIEP", nullable = false, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String tenDoanhNghiep;

    @Column(name = "MA_SO_THUE", nullable = true, length = 20)
    String maSoThue;

    @Column(name = "NGUOI_DAI_DIEN", columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String nguoiDaiDien;

    @Column(name = "DIA_CHI", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String diaChi;

    @Column(name = "EMAIL")
    String email;

    @Column(name = "SO_DIEN_THOAI", length = 15)
    String soDienThoai;

    @Column(name = "NGANH_NGHE", columnDefinition = "VARCHAR(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String nganhNghe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_NHAN_VIEN")
    NhanVien nhanVien;

    @Builder.Default @OneToMany(mappedBy = "doanhNghiep")
    @JsonIgnore @ToString.Exclude @EqualsAndHashCode.Exclude
    List<LichSuQna> lichSuQnas = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    LocalDateTime updatedAt;
}
