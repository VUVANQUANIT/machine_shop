package com.example.machinesshop.dto.product;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductDTORequestUpdate {

    @Size(min = 2, max = 200, message = "Product name must be between 2 and 200 characters")
    private String name;

    @Size(max = 250, message = "Slug cannot exceed 250 characters")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
            message = "Slug must contain only lowercase letters, numbers, and hyphens")
    private String slug;

    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;

    @Min(value = 0, message = "Quantity cannot be negative")
    @Max(value = 999999, message = "Quantity is too large")
    private Integer quantity;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "99999999.99", message = "Price is too large")
    private BigDecimal price;

    private String status;

    private Long categoryId;

    private Boolean isActive = true;
}
