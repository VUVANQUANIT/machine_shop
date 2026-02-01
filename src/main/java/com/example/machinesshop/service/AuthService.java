package com.example.machinesshop.service;

import com.example.machinesshop.dto.auth.LoginRequestDTO;
import com.example.machinesshop.dto.auth.LoginResponseDTO;
import com.example.machinesshop.dto.auth.RefreshRequestDTO;
import com.example.machinesshop.dto.auth.RegisterRequestDTO;
import com.example.machinesshop.entity.RefreshToken;
import com.example.machinesshop.entity.User;
import com.example.machinesshop.exception.InvalidCredentialsException;
import com.example.machinesshop.exception.InvalidTokenException;
import com.example.machinesshop.repository.RefreshTokenRepository;
import com.example.machinesshop.repository.UserRepository;
import com.example.machinesshop.security.JwtConfig;
import com.example.machinesshop.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Tên đăng nhập hoặc mật khẩu không đúng."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Tên đăng nhập hoặc mật khẩu không đúng.");
        }
        return issueTokens(user);
    }

    @Transactional
    public LoginResponseDTO register(RegisterRequestDTO request) {
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("Mật khẩu và xác nhận mật khẩu không khớp.");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.UserRole.USER);
        user = userRepository.save(user);
        log.info("User registered: {}", user.getUsername());
        return issueTokens(user);
    }

    /**
     * Cấp access_token mới khi client gửi refresh_token hợp lệ.
     */
    @Transactional
    public LoginResponseDTO refresh(RefreshRequestDTO request) {
        String tokenValue = request.getRefreshToken();
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalseAndExpiryDateAfter(
                        tokenValue, Instant.now())
                .orElseThrow(() -> new InvalidTokenException("Refresh token không hợp lệ hoặc đã hết hạn. Vui lòng đăng nhập lại."));
        User user = refreshToken.getUser();
        revokeRefreshToken(refreshToken);
        return issueTokens(user);
    }

    private LoginResponseDTO issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getRole().name());
        String refreshTokenValue = createAndSaveRefreshToken(user);
        long expiresIn = jwtService.getAccessTokenExpirationSeconds();
        return new LoginResponseDTO(accessToken, refreshTokenValue, expiresIn);
    }

    private String createAndSaveRefreshToken(User user) {
        refreshTokenRepository.revokeAllByUser(user);
        String tokenValue = UUID.randomUUID().toString().replace("-", "") + "-" + UUID.randomUUID().toString().replace("-", "");
        RefreshToken rt = new RefreshToken();
        rt.setToken(tokenValue);
        rt.setUser(user);
        rt.setExpiryDate(Instant.now().plusMillis(jwtConfig.getRefreshExpiration()));
        rt.setRevoked(false);
        refreshTokenRepository.save(rt);
        return tokenValue;
    }

    private void revokeRefreshToken(RefreshToken rt) {
        rt.setRevoked(true);
        refreshTokenRepository.save(rt);
    }
}
