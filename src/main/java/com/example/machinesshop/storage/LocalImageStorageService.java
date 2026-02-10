package com.example.machinesshop.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Fallback implementation: lưu ảnh vào local filesystem.
 * Chỉ được sử dụng khi không có CloudinaryImageStorageService (không có Cloudinary config).
 * Tuân thủ SOLID - Single Responsibility: chỉ lo upload/delete image local.
 */
@Slf4j
@Service
@ConditionalOnMissingBean(CloudinaryImageStorageService.class)
public class LocalImageStorageService implements ImageStorageService {

    @Value("${app.upload.directory:uploads}")
    private String uploadDirectory;

    @Override
    public String uploadImage(MultipartFile file, String folder) {
        try {
            if (file == null || file.isEmpty()) {
                throw new ImageStorageException("File không được để trống");
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String fileName = UUID.randomUUID() + extension;

            // Create directory structure: uploads/{folder}/
            Path folderPath = Paths.get(uploadDirectory, folder);
            Files.createDirectories(folderPath);

            // Save file
            Path filePath = folderPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            // Return relative URL (Spring sẽ serve qua StaticConfig)
            String imageUrl = "/uploads/" + folder + "/" + fileName;
            log.info("Upload thành công (local): {} -> {}", originalFilename, imageUrl);
            return imageUrl;

        } catch (Exception e) {
            log.error("Lỗi khi upload image local: {}", e.getMessage(), e);
            throw new ImageStorageException("Upload thất bại: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteImage(String imageUrl) {
        try {
            // Extract path từ URL: /uploads/products/uuid.jpg -> uploads/products/uuid.jpg
            String pathStr = imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl;
            Path filePath = Paths.get(pathStr);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Xóa thành công image local: {}", filePath);
                return true;
            } else {
                log.warn("File không tồn tại: {}", filePath);
                return false;
            }
        } catch (Exception e) {
            log.error("Lỗi khi xóa image local: {}", e.getMessage(), e);
            return false;
        }
    }
}
