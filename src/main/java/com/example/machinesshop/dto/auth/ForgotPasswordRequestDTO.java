package com.example.machinesshop.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequestDTO {
    @Email(message = "Bạn phải nhập định dạng email")
    @NotBlank
    @Size(min = 6, max = 50,message = "Email phải nhập từ 6 đến dưới 50 kí tự")
    private String email;
}
