package com.example.machinesshop.exception;

/**
 * Sai tên đăng nhập hoặc mật khẩu.
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
