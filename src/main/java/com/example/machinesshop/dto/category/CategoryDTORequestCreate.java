package com.example.machinesshop.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTORequestCreate {
    @NotBlank(message = "Tên loại sản phẩm không được để trống")
    @Size(min = 1, max = 100, message = "Tên loại sản phẩm từ 1 đến 100 ký tự")
    private String name;
}
