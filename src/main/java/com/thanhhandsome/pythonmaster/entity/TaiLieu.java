package com.thanhhandsome.pythonmaster.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TAILIEU")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaiLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "ID_TAI_LIEU", nullable = false, unique = true, length = 50)
    String idTaiLieu;

    @Column(name = "TEN_TAI_LIEU", nullable = false, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String tenTaiLieu;

    @Column(name = "LOAI_TAI_LIEU", nullable = false, length = 30)
    String loaiTaiLieu;

    @Column(name = "URL_LUU_TRU", nullable = false, columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String urlLuuTru;

    @Column(name = "MO_TA", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String moTa;

    @Column(name = "TU_KHOA_AI", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String tuKhoaAi;

    @Column(name = "TRANG_THAI", nullable = false, length = 30)
    String trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY")
    NhanVien createdBy;

    @Builder.Default @OneToMany(mappedBy = "taiLieu")
    @JsonIgnore @ToString.Exclude @EqualsAndHashCode.Exclude
    List<LichSuQna> lichSuQnas = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    LocalDateTime createdAt;
}
