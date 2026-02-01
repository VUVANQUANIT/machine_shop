package com.example.machinesshop.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Trả 401 JSON thống nhất khi chưa đăng nhập (không có token hoặc token không gửi kèm).
 * Frontend nhận code "UNAUTHORIZED" hoặc "NO_TOKEN" để redirect login hoặc thử refresh.
 */
@Component
@RequiredArgsConstructor
public class Http401EntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", 401,
                "error", "Unauthorized",
                "code", "UNAUTHORIZED",
                "message", "Chưa đăng nhập hoặc token không hợp lệ. Gửi Authorization: Bearer <access_token> hoặc dùng refresh token.",
                "path", request.getRequestURI()
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
