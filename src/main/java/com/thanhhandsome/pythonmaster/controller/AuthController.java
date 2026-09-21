package com.thanhhandsome.pythonmaster.controller;

import com.thanhhandsome.pythonmaster.dto.request.LoginRequest;
import com.thanhhandsome.pythonmaster.dto.response.ApiResponse;
import com.thanhhandsome.pythonmaster.dto.response.LoginResponse;
import com.thanhhandsome.pythonmaster.entity.Admin;
import com.thanhhandsome.pythonmaster.security.JwtUtil;
import com.thanhhandsome.pythonmaster.security.TokenBlacklistService;
import com.thanhhandsome.pythonmaster.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Đăng nhập / đăng xuất")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final AdminService adminService;

    /**
     * POST /api/v1/auth/login
     * Body: { "username": "admin", "password": "admin123" }
     */
    @PostMapping("/login")
    @Operation(summary = "Đăng nhập", description = "Trả về JWT accessToken")
    @SecurityRequirements
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        // Xác thực admin từ database
        boolean authenticated = adminService.authenticate(request.getUsername(), request.getPassword());

        if (!authenticated) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Tên đăng nhập hoặc mật khẩu không đúng."));
        }

        String token = jwtUtil.generateToken(request.getUsername());

        LoginResponse data = LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationMs())
                .username(request.getUsername())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", data));
    }

    /**
     * POST /api/v1/auth/logout
     * Header: Authorization Bearer access token.
     * Thu hồi token hiện tại — request sau với token này sẽ bị 401.
     */
    @PostMapping("/logout")
    @Operation(summary = "Đăng xuất", description = "Thu hồi JWT hiện tại vào blacklist")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Thiếu token đăng nhập."));
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.isValid(token) || tokenBlacklistService.isRevoked(token)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Token không hợp lệ hoặc đã đăng xuất."));
        }

        Date expiration = jwtUtil.getExpiration(token);
        long expiresAt = expiration != null
                ? expiration.getTime()
                : System.currentTimeMillis() + jwtUtil.getExpirationMs();
        tokenBlacklistService.revoke(token, expiresAt);

        return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công", null));
    }
}
