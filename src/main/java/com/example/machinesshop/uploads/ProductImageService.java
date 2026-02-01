package com.example.machinesshop.uploads;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {
    void uploadImages(Long productId, List<MultipartFile> files);
}
