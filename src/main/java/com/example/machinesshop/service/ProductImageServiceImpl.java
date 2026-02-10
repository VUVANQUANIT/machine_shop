package com.example.machinesshop.service;

import com.example.machinesshop.entity.ProductImage;
import com.example.machinesshop.exception.ResourceNotFoundException;
import com.example.machinesshop.repository.ProductImageRepository;
import com.example.machinesshop.repository.ProductRepository;
import com.example.machinesshop.storage.ImageStorageService;
import com.example.machinesshop.uploads.ProductImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Implementation của ProductImageService.
 * Tuân thủ SOLID:
 * - Single Responsibility: chỉ lo business logic của product images (không lo storage details)
 * - Dependency Inversion: phụ thuộc vào ImageStorageService interface, không phụ thuộc vào implementation cụ thể
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ImageStorageService imageStorageService; // Dependency Inversion: inject interface, không inject concrete class

    @Override
    public void uploadImages(Long productId, List<MultipartFile> files) {
        // Validate product exists
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId);
        }

        // Upload each file và lưu URL vào database
        for (MultipartFile file : files) {
            try {
                // Upload to storage (Cloudinary hoặc Local tùy implementation được inject)
                String imageUrl = imageStorageService.uploadImage(file, "products");

                // Save to database
                ProductImage productImage = new ProductImage();
                productImage.setProductId(productId);
                productImage.setImageUrl(imageUrl);

                productImageRepository.save(productImage);
                log.info("Đã lưu image cho product {}: {}", productId, imageUrl);

            } catch (Exception e) {
                log.error("Lỗi khi upload image cho product {}: {}", productId, e.getMessage(), e);
                throw new RuntimeException("Upload ảnh thất bại: " + e.getMessage(), e);
            }
        }
    }
}
