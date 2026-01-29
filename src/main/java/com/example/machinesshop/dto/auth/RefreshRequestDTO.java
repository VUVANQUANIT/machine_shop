package com.example.machinesshop.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshRequestDTO {
    @NotBlank(message = "Refresh token không được để trống")
    private String refreshToken;
}
