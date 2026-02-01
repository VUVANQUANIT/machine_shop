package com.example.machinesshop.controller;

import com.example.machinesshop.dto.auth.LoginRequestDTO;
import com.example.machinesshop.dto.auth.LoginResponseDTO;
import com.example.machinesshop.dto.auth.RefreshRequestDTO;
import com.example.machinesshop.dto.auth.RegisterRequestDTO;
import com.example.machinesshop.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Đăng ký, đăng nhập, refresh token")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập", description = "Trả về access_token (JWT) và refresh_token. Access token dùng trong header Authorization.")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Đăng ký", description = "Tạo tài khoản mới và trả về access_token + refresh_token.")
    public ResponseEntity<LoginResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Làm mới access token", description = "Gửi refresh_token trong body. Trả về access_token mới (và refresh_token mới). Gọi khi nhận 401 với code TOKEN_EXPIRED.")
    public ResponseEntity<LoginResponseDTO> refresh(@Valid @RequestBody RefreshRequestDTO request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}
