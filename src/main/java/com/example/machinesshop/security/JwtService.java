package com.example.machinesshop.security;

import com.example.machinesshop.exception.InvalidTokenException;
import com.example.machinesshop.exception.TokenExpiredException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    private final JwtConfig jwtConfig;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Tạo JWT access token (ngắn hạn, 15–30 phút).here is the code to generate access token
     */
    public String generateAccessToken(String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtConfig.getExpiration());
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Xác thực access token; trả về username nếu hợp lệ.
     * @throws TokenExpiredException nếu token hết hạn (frontend gọi /refresh).
     * @throws InvalidTokenException nếu token sai format hoặc chữ ký không hợp lệ.
     */
    public String validateAccessTokenAndGetUsername(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            log.debug("Access token expired for subject: {}", e.getClaims().getSubject());
            throw new TokenExpiredException("Access token đã hết hạn. Vui lòng dùng refresh token để lấy token mới.");
        } catch (SignatureException | IllegalArgumentException e) {
            log.debug("Invalid access token: {}", e.getMessage());
            throw new InvalidTokenException("Token không hợp lệ.");
        }
    }

    /**
     * Trả về thời gian hết hạn access token (giây) để client biết khi cần refresh.
     */
    public long getAccessTokenExpirationSeconds() {
        return jwtConfig.getExpiration() / 1000;
    }
}
