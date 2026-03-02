package com.example.machinesshop.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordResponseDTO {
    private String resetToken;
    private long expiresInSeconds;
}

