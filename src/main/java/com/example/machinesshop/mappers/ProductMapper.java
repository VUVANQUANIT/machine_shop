package com.example.machinesshop.mappers;

import com.example.machinesshop.ENUM.ProductStatus;
import com.example.machinesshop.dto.product.ProductDTO;
import com.example.machinesshop.dto.product.ProductDTORequestCreate;
import com.example.machinesshop.dto.product.ProductDTORequestUpdate;
import com.example.machinesshop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper{
    @Mapping(target = "status", expression = "java(toProductStatus(dto.getStatus()))")
    Product toEntity(ProductDTORequestCreate dto);

    @Mapping(target = "status", expression = "java(toProductStatus(dto.getStatus()))")
    void updateEntity(@MappingTarget Product product, ProductDTORequestUpdate dto);
    ProductDTO toResponseDTO(Product product);

    List<ProductDTO> toResponseDTO(List<Product> products);

    // THÊM: Xử lý Enum conversion
    default ProductStatus toProductStatus(String status) {
        if (status == null) return null;
        try {
            return ProductStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    // THÊM: Xử lý null safety
    default void updateProductFromDTO(Product product, ProductDTORequestUpdate dto) {
        if (dto == null) return;

        // Chỉ update những field không null
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getSlug() != null) product.setSlug(dto.getSlug());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getQuantity() != null) product.setQuantity(dto.getQuantity());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getStatus() != null) product.setStatus(toProductStatus(dto.getStatus()));
        if (dto.getCategoryId() != null) product.setCategoryId(dto.getCategoryId());
        if (dto.getIsActive() != null) product.setIsActive(dto.getIsActive());
    }
}
