package com.example.machinesshop.service;


import com.example.machinesshop.entity.User;
import com.example.machinesshop.exception.InvalidTokenException;
import com.example.machinesshop.exception.TokenExpiredException;
import com.example.machinesshop.repository.RefreshTokenRepository;
import com.example.machinesshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {
    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final Duration RESET_TOKEN_TTL = Duration.ofMinutes(10);
    private static final Duration OTP_ATTEMPT_TTL = Duration.ofMinutes(5);
    private static final long MAX_OTP_ATTEMPTS = 5;

    private static final String OTP_KEY_PREFIX = "reset:otp:";
    private static final String OTP_ATTEMPT_KEY_PREFIX = "reset:otp:attempts:";
    private static final String RESET_TOKEN_KEY_PREFIX = "reset:password:";

    private final RedisTemplate<String, String> redisTemplate;
    private final EmailQueueService emailQueueService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public void createResetToken(String email) {
        String normalizedEmail = normalizeEmail(email);
        if (userRepository.findByEmail(normalizedEmail).isEmpty()) {
            return;
        }

        String otp = generateOtp();
        String otpKey = OTP_KEY_PREFIX + normalizedEmail;
        String attemptKey = OTP_ATTEMPT_KEY_PREFIX + normalizedEmail;

        redisTemplate.opsForValue().set(otpKey, sha256(otp), OTP_TTL);
        redisTemplate.delete(attemptKey);
        emailQueueService.SendEmailResetPassword(normalizedEmail, otp);
    }

    public String verifyOtp(String email, String otp) {
        String normalizedEmail = normalizeEmail(email);
        String otpKey = OTP_KEY_PREFIX + normalizedEmail;
        String attemptKey = OTP_ATTEMPT_KEY_PREFIX + normalizedEmail;
        String savedOtpHash = redisTemplate.opsForValue().get(otpKey);

        if (savedOtpHash == null) {
            throw new TokenExpiredException("OTP đã hết hạn hoặc không tồn tại.");
        }

        String inputOtpHash = sha256(otp);
        if (!savedOtpHash.equals(inputOtpHash)) {
            Long attempts = redisTemplate.opsForValue().increment(attemptKey);
            if (attempts != null && attempts == 1L) {
                redisTemplate.expire(attemptKey, OTP_ATTEMPT_TTL);
            }
            if (attempts != null && attempts >= MAX_OTP_ATTEMPTS) {
                redisTemplate.delete(otpKey);
                redisTemplate.delete(attemptKey);
                throw new InvalidTokenException("OTP không hợp lệ. Vui lòng yêu cầu mã mới.");
            }
            throw new InvalidTokenException("OTP không hợp lệ.");
        }

        String resetToken = UUID.randomUUID().toString().replace("-", "");
        String resetKey = RESET_TOKEN_KEY_PREFIX + resetToken;
        redisTemplate.opsForValue().set(resetKey, normalizedEmail, RESET_TOKEN_TTL);

        redisTemplate.delete(otpKey);
        redisTemplate.delete(attemptKey);
        return resetToken;
    }

    @Transactional
    public void resetPassword(String resetToken, String newPassword) {
        String resetKey = RESET_TOKEN_KEY_PREFIX + resetToken;
        String email = redisTemplate.opsForValue().get(resetKey);

        if (email == null) {
            throw new TokenExpiredException("Reset token đã hết hạn hoặc không hợp lệ.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản phù hợp."));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        refreshTokenRepository.revokeAllByUser(user);
        redisTemplate.delete(resetKey);
    }

    public boolean validateToken(String token) {
        String redisKey = RESET_TOKEN_KEY_PREFIX + token;
        return redisTemplate.opsForValue().get(redisKey) != null;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String generateOtp() {
        int number = ThreadLocalRandom.current().nextInt(100_000, 1_000_000);
        return String.valueOf(number);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Không thể mã hóa OTP.", e);
        }
    }
}
