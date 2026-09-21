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
@Table(name = "CUOCTHI")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuocThi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "ID_CUOC_THI", nullable = false, unique = true, length = 50)
    String idCuocThi;

    @Column(name = "TEN_CUOC_THI", nullable = false, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String tenCuocThi;

    @Column(name = "MO_TA", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String moTa;

    @Column(name = "HAN_DANG_KY")
    LocalDateTime hanDangKy;

    @Column(name = "NGAY_BAT_DAU")
    LocalDateTime ngayBatDau;

    @Column(name = "NGAY_KET_THUC")
    LocalDateTime ngayKetThuc;

    @Builder.Default @OneToMany(mappedBy = "cuocThi")
    @JsonIgnore @ToString.Exclude @EqualsAndHashCode.Exclude
    List<DangKy> dangKys = new ArrayList<>();

    @Builder.Default @OneToMany(mappedBy = "cuocThi")
    @JsonIgnore @ToString.Exclude @EqualsAndHashCode.Exclude
    List<BaiTap> baiTaps = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    LocalDateTime createdAt;
}
