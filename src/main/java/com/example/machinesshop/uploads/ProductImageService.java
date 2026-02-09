package com.example.machinesshop.uploads;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface cho product image operations.
 * Tuân thủ SOLID - Single Responsibility: chỉ lo business logic của product images.
 */
public interface ProductImageService {
    
    /**
     * Upload nhiều images cho một product và lưu vào database.
     *
     * @param productId ID của product
     * @param files Danh sách files cần upload
     */
    void uploadImages(Long productId, List<MultipartFile> files);
}
