package com.thanhhandsome.pythonmaster.service;

import com.thanhhandsome.pythonmaster.dto.request.UpdateAdminRequest;
import com.thanhhandsome.pythonmaster.entity.Admin;
import com.thanhhandsome.pythonmaster.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Tạo admin mặc định nếu chưa tồn tại
     */
    @Transactional
    public void createDefaultAdminIfNeeded() {
        if (adminRepository.count() == 0) {
            Admin defaultAdmin = Admin.builder()
                    .username("admin")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .hoTen("Administrator")
                    .email("admin@pythonmaster.vn")
                    .build();
            adminRepository.save(defaultAdmin);
            log.info("Đã tạo admin mặc định: username=admin, password=admin123");
        }
    }

    /**
     * Cập nhật thông tin admin
     */
    @Transactional
    public Admin updateAdmin(UpdateAdminRequest request) {
        // Tìm admin hiện tại (chỉ có 1 admin trong hệ thống)
        Admin admin = adminRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy admin trong hệ thống"));

        // Kiểm tra username mới có trùng với admin khác không
        if (!admin.getUsername().equals(request.getUsername()) 
                && adminRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }

        // Cập nhật thông tin
        admin.setUsername(request.getUsername());
        admin.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        admin.setHoTen(request.getHoTen());
        admin.setEmail(request.getEmail());
        admin.setSoDienThoai(request.getSoDienThoai());

        return adminRepository.save(admin);
    }

    /**
     * Lấy thông tin admin hiện tại
     */
    @Transactional(readOnly = true)
    public Optional<Admin> getCurrentAdmin() {
        return adminRepository.findAll().stream().findFirst();
    }

    /**
     * Xác thực admin
     */
    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        return adminRepository.findByUsername(username)
                .map(admin -> passwordEncoder.matches(password, admin.getPasswordHash()))
                .orElse(false);
    }
}
