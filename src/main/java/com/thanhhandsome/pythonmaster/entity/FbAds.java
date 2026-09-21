package com.thanhhandsome.pythonmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "FBADS")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FbAds {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AD_LOG")
    Long adLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_THI_SINH")
    ThiSinh thiSinh;

    @Column(name = "TEN_QUANG_CAO", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String tenQuangCao;

    @Column(name = "ID_SET_QUANG_CAO", length = 100)
    String idSetQuangCao;

    @Column(name = "CHIEN_DICH", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String chienDich;

    @Column(name = "ID_FORM", length = 50)
    String idForm;

    @Column(name = "NEN_TANG", columnDefinition = "VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String nenTang;

    @Column(name = "URL_INBOX", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String urlInbox;

    @Builder.Default
    @Column(name = "TRANG_THAI_GHI_DANH", nullable = false, length = 20)
    String trangThaiGhiDanh = "new";

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    LocalDateTime createdAt;
}