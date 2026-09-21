package com.thanhhandsome.pythonmaster.controller;

import com.thanhhandsome.pythonmaster.configuration.OpenApiConfig;
import com.thanhhandsome.pythonmaster.dto.request.UpdateAdminRequest;
import com.thanhhandsome.pythonmaster.dto.response.ApiResponse;
import com.thanhhandsome.pythonmaster.entity.Admin;
import com.thanhhandsome.pythonmaster.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Quản lý thông tin admin")
public class AdminController {

    private final AdminService adminService;

    /**
     * GET /api/v1/admin/current
     * Lấy thông tin admin hiện tại
     */
    @GetMapping("/current")
    @Operation(summary = "Lấy thông tin admin hiện tại", description = "Trả về thông tin admin duy nhất trong hệ thống")
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    public ResponseEntity<ApiResponse<Admin>> getCurrentAdmin() {
        Optional<Admin> admin = adminService.getCurrentAdmin();
        if (admin.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin admin thành công", admin.get()));
        } else {
            return ResponseEntity.ok(ApiResponse.success("Chưa có admin trong hệ thống", null));
        }
    }

    /**
     * PUT /api/v1/admin/update
     * Cập nhật thông tin admin (username, password, họ tên, email, sđt)
     */
    @PutMapping("/update")
    @Operation(summary = "Cập nhật thông tin admin", description = "Cập nhật username, password và thông tin cá nhân của admin")
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    public ResponseEntity<ApiResponse<Admin>> updateAdmin(
            @Valid @RequestBody UpdateAdminRequest request) {
        try {
            Admin updatedAdmin = adminService.updateAdmin(request);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin admin thành công", updatedAdmin));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(500, "Lỗi khi cập nhật thông tin admin: " + e.getMessage()));
        }
    }
}
