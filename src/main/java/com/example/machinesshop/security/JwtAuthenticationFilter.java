package com.example.machinesshop.security;

import com.example.machinesshop.entity.User;
import com.example.machinesshop.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);
        try {
            String username = jwtService.validateAccessTokenAndGetUsername(token);
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }
            User user = userOpt.get();
            var authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
            var auth = new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            // TokenExpiredException / InvalidTokenException xử lý tại filter không set response 401 ở đây;
            // để request đi tiếp, endpoint được bảo vệ sẽ trả 403. Để trả 401 với body JSON có code,
            // ta xử lý trong filter: không set auth, và nếu là request tới API cần auth thì trả 401.
            // Cách đơn giản: ném exception → Entry point hoặc filter sau bắt. Spring Security khi
            // không có auth sẽ gọi AuthenticationEntryPoint → 401. Nhưng exception từ filter không
            // tự động thành 401 body. Best: để filter không set auth khi lỗi, request tiếp tục;
            // các endpoint protected sẽ không có Principal → SecurityContext empty → Spring gọi
            // AuthenticationEntryPoint → 401. Nhưng body 401 mặc định là HTML hoặc empty.
            // Để trả JSON 401 với code, ta cần CustomAuthenticationEntryPoint và có cách nhận biết
            // "token expired" vs "no token". Cách hay: trong filter khi catch TokenExpiredException
            // hoặc InvalidTokenException, set attribute vào request, sau đó AuthenticationEntryPoint
            // đọc attribute và trả body với code. Hoặc filter trực tiếp gửi 401 JSON và không
            // gọi filterChain.doFilter khi path cần auth. Đơn giản hơn: luôn doFilter; entry point
            // chỉ trả 401 Unauthorized; exception handler chỉ bắt khi exception được ném tới controller.
            // Exception ném trong filter không tới controller. Vậy ta cần: (1) filter không ném,
            // chỉ clear context và doFilter; (2) AuthenticationEntryPoint tùy chỉnh trả JSON 401
            // với message chung "Unauthorized". (3) Hoặc filter với path /api/admin, /api/... (protected):
            // nếu có Bearer nhưng invalid/expired → response 401 JSON với code. Cách (3) rõ ràng cho
            // frontend: gửi Bearer nhưng token expired → 401 + code TOKEN_EXPIRED.
            SecurityContextHolder.clearContext();
            if (isProtectedPath(request.getRequestURI())) {
                sendUnauthorizedJson(response, e);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isProtectedPath(String path) {
        return path.startsWith("/api/admin") || path.startsWith("/api/auth/me");
    }

    private void sendUnauthorizedJson(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String code = e instanceof com.example.machinesshop.exception.TokenExpiredException
                ? "TOKEN_EXPIRED"
                : "INVALID_TOKEN";
        String message = e.getMessage() != null ? e.getMessage() : "Unauthorized";
        String body = String.format(
                "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"Unauthorized\",\"code\":\"%s\",\"message\":\"%s\",\"path\":\"\"}",
                java.time.LocalDateTime.now(), code, message.replace("\"", "\\\""));
        response.getWriter().write(body);
    }
}
