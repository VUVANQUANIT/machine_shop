package com.example.machinesshop.service;

import com.example.machinesshop.dto.product.SpecEntryDTO;
import com.example.machinesshop.entity.ProductSpecification;
import com.example.machinesshop.exception.ResourceNotFoundException;
import com.example.machinesshop.repository.ProductRepository;
import com.example.machinesshop.repository.ProductSpeccificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ProductSpecificationService {
    private final ProductRepository productRepository;
    private final ProductSpeccificationRepository productSpecificationRepository;

    /**
     * Thêm danh sách thông số kỹ thuật cho sản phẩm. Mỗi phần tử là cặp specKey – specValue.
     */
    public void addSpecifications(Long productId, List<SpecEntryDTO> specs) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId);
        }
        if (specs == null || specs.isEmpty()) return;
        for (SpecEntryDTO dto : specs) {
            ProductSpecification spec = new ProductSpecification();
            spec.setProductId(productId);
            spec.setSpecKey(dto.getSpecKey().trim());
            spec.setSpecValue(dto.getSpecValue().trim());
            productSpecificationRepository.save(spec);
        }
        log.info("Added {} specification(s) for product id={}", specs.size(), productId);
    }
}
