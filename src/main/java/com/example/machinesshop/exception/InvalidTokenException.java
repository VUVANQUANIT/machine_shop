package com.example.machinesshop.exception;

/**
 * Token không hợp lệ (sai format, đã bị thu hồi, hoặc không tồn tại).
 */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
