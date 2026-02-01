package com.example.machinesshop.exception;

/**
 * Access token đã hết hạn. Frontend nên gọi /api/auth/refresh với refresh_token để lấy access_token mới.
 */
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
