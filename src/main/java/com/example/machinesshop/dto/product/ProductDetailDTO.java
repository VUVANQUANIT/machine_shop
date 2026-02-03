package com.example.machinesshop.dto.product;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDetailDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private List<String> images;
    private String categoryName;
    /** Thông số kỹ thuật – hiển thị dạng list (mỗi phần tử: specKey + specValue). */
    private List<SpecEntryDTO> specifications;
}
