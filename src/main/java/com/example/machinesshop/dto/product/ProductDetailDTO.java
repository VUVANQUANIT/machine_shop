package com.example.machinesshop.dto.product;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDetailDTO {
    Long id;
    String name;
    BigDecimal price;
    List<String> images;
    String categoryName;
}
