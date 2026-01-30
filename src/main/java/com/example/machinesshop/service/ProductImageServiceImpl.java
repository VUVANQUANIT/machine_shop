package com.example.machinesshop.service;

import com.example.machinesshop.entity.Product;
import com.example.machinesshop.entity.ProductImage;
import com.example.machinesshop.exception.ResourceNotFoundException;
import com.example.machinesshop.repository.ProductImageRepository;
import com.example.machinesshop.repository.ProductRepository;
import com.example.machinesshop.uploads.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private boolean existsByProductId(Long productId) {
        return productRepository.existsById(productId);
    }
    @Override
    public void uploadImages(Long productId, List<MultipartFile> files) {
//        Product  product = productRepository.findById(productId)
//                .orElseThrow(() -> new ResourceNotFoundException(String.format("Không tìm thấy sản phẩm để up ảnh")));
        for (MultipartFile file : files) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path path = Paths.get("uploads", "products", fileName);

            try {
                Files.createDirectories(path.getParent());
                file.transferTo(path);
            }
            catch(Exception e) {
                throw new RuntimeException("UPLOAD FILE FAILED");
            }
            ProductImage productImage = new ProductImage();
            productImage.setProductId(productId);
            productImage.setImageUrl("/uploads/products/" + fileName);

            productImageRepository.save(productImage);
        }
    }
}
