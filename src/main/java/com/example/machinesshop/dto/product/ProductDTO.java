package com.example.machinesshop.dto.product;

import com.example.machinesshop.ENUM.ProductStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDTO {

    private Long id;

    private String name;

    private String slug;

    private String description;

    private Integer quantity;

    private BigDecimal price;

    private ProductStatus status;

    private Long categoryId;

    private Boolean isActive = true;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;
}
