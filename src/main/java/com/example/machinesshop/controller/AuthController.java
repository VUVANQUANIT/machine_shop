package com.example.machinesshop.controller;

import com.example.machinesshop.dto.ApiResponse;
import com.example.machinesshop.dto.auth.ForgotPasswordRequestDTO;
import com.example.machinesshop.dto.auth.ForgotPasswordResponseDTO;
import com.example.machinesshop.dto.auth.ForgotPasswordVerifyRequestDTO;
import com.example.machinesshop.dto.auth.LoginRequestDTO;
import com.example.machinesshop.dto.auth.LoginResponseDTO;
import com.example.machinesshop.dto.auth.RefreshRequestDTO;
import com.example.machinesshop.dto.auth.RegisterRequestDTO;
import com.example.machinesshop.dto.auth.ResetPasswordRequestDTO;
import com.example.machinesshop.service.AuthService;
import com.example.machinesshop.service.ForgotPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Dang ky, dang nhap, refresh token, forgot password")
public class AuthController {
    private final AuthService authService;
    private final ForgotPasswordService forgotPasswordService;

    @PostMapping("/login")
    @Operation(summary = "Dang nhap")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Dang ky")
    public ResponseEntity<LoginResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Lam moi access token")
    public ResponseEntity<LoginResponseDTO> refresh(@Valid @RequestBody RefreshRequestDTO request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/forgot-password/request")
    @Operation(
            summary = "Yeu cau OTP reset password",
            description = "Gui email, he thong se gui OTP neu tai khoan ton tai. Luon tra ve thong diep chung."
    )
    public ResponseEntity<ApiResponse<Void>> requestForgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO request
    ) {
        forgotPasswordService.createResetToken(request.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Neu email ton tai, OTP da duoc gui.", null));
    }

    @PostMapping("/forgot-password/verify-otp")
    @Operation(
            summary = "Xac thuc OTP",
            description = "Xac thuc OTP va tra ve reset token ngan han de doi mat khau."
    )
    public ResponseEntity<ApiResponse<ForgotPasswordResponseDTO>> verifyOtp(
            @Valid @RequestBody ForgotPasswordVerifyRequestDTO request
    ) {
        String resetToken = forgotPasswordService.verifyOtp(request.getEmail(), request.getOtp());
        ForgotPasswordResponseDTO data = new ForgotPasswordResponseDTO(resetToken, 600);
        return ResponseEntity.ok(ApiResponse.ok("Xac thuc OTP thanh cong.", data));
    }

    @PostMapping("/forgot-password/reset")
    @Operation(
            summary = "Dat mat khau moi",
            description = "Dung reset token de cap nhat mat khau moi."
    )
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO request
    ) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mat khau va xac nhan mat khau khong khop.");
        }
        forgotPasswordService.resetPassword(request.getResetToken(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.ok("Dat lai mat khau thanh cong.", null));
    }
}

