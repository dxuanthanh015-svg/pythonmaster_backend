package com.thanhhandsome.pythonmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "THANHTOAN")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DANG_KY", nullable = false, unique = true)
    DangKy dangKy;

    @Column(name = "THOI_GIAN_GIAO_DICH")
    LocalDateTime thoiGianGiaoDich;

    @Column(name = "BANG_DAU", nullable = false, length = 20)
    String bangDau;

    @Column(name = "SO_TIEN", nullable = false, precision = 12, scale = 2)
    BigDecimal soTien;

    @Builder.Default
    @Column(name = "PAYMENT_STATUS", nullable = false, length = 20)
    String paymentStatus = "pending";

    @Column(name = "ID_GIAO_DICH", unique = true, length = 100)
    String idGiaoDich;

    @Column(name = "PARTNER_DATE")
    LocalDateTime partnerDate;

    @Column(name = "GHI_CHU", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String ghiChu;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    LocalDateTime createdAt;
}