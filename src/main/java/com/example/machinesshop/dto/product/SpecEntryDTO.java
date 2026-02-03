package com.example.machinesshop.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecEntryDTO {
    @NotBlank(message = "Tên thông số không được để trống")
    @Size(max = 100, message = "Tên thông số tối đa 100 ký tự")
    private String specKey;

    @NotBlank(message = "Giá trị thông số không được để trống")
    @Size(max = 500, message = "Giá trị thông số tối đa 500 ký tự")
    private String specValue;
}
