package com.thanhhandsome.pythonmaster.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ADMIN")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "USERNAME", nullable = false, unique = true, length = 50)
    String username;

    @Column(name = "PASSWORD_HASH", nullable = false, length = 255)
    String passwordHash;

    @Column(name = "HO_TEN", columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String hoTen;

    @Column(name = "EMAIL", length = 100)
    String email;

    @Column(name = "SO_DIEN_THOAI", length = 15)
    String soDienThoai;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    LocalDateTime updatedAt;
}
