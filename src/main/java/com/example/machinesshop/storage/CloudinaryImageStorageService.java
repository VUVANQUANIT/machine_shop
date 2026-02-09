package com.example.machinesshop.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation của ImageStorageService sử dụng Cloudinary.
 * Tuân thủ SOLID - Single Responsibility: chỉ lo upload/delete image.
 * Chỉ được tạo khi Cloudinary bean có sẵn (có config).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@org.springframework.boot.autoconfigure.condition.ConditionalOnBean(Cloudinary.class)
public class CloudinaryImageStorageService implements ImageStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file, String folder) {
        try {
            // Validate file
            if (file == null || file.isEmpty()) {
                throw new ImageStorageException("File không được để trống");
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String publicId = folder + "/" + UUID.randomUUID() + extension;

            // Upload to Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "folder", folder,
                            "resource_type", "image",
                            "overwrite", false
                    )
            );

            // Extract secure URL (HTTPS)
            String imageUrl = (String) uploadResult.get("secure_url");
            if (imageUrl == null) {
                imageUrl = (String) uploadResult.get("url");
            }

            log.info("Upload thành công: {} -> {}", originalFilename, imageUrl);
            return imageUrl;

        } catch (IOException e) {
            log.error("Lỗi khi đọc file: {}", e.getMessage(), e);
            throw new ImageStorageException("Không thể đọc file: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Lỗi khi upload lên Cloudinary: {}", e.getMessage(), e);
            throw new ImageStorageException("Upload thất bại: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteImage(String imageUrl) {
        try {
            // Extract public_id from URL
            // Cloudinary URL format: https://res.cloudinary.com/{cloud_name}/image/upload/{version}/{public_id}.{format}
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId == null) {
                log.warn("Không thể extract public_id từ URL: {}", imageUrl);
                return false;
            }

            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Xóa thành công image: {}", publicId);
            return true;

        } catch (Exception e) {
            log.error("Lỗi khi xóa image từ Cloudinary: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Extract public_id từ Cloudinary URL.
     * Ví dụ: https://res.cloudinary.com/demo/image/upload/v1234567890/products/uuid.jpg
     * -> products/uuid
     */
    private String extractPublicIdFromUrl(String url) {
        try {
            // Tìm phần sau /upload/ và trước extension
            int uploadIndex = url.indexOf("/upload/");
            if (uploadIndex == -1) return null;

            String afterUpload = url.substring(uploadIndex + "/upload/".length());
            // Bỏ qua version nếu có (v1234567890/)
            if (afterUpload.matches("^v\\d+/.*")) {
                afterUpload = afterUpload.substring(afterUpload.indexOf('/') + 1);
            }
            // Bỏ extension
            int lastDot = afterUpload.lastIndexOf('.');
            if (lastDot != -1) {
                afterUpload = afterUpload.substring(0, lastDot);
            }
            return afterUpload;
        } catch (Exception e) {
            log.warn("Không thể parse public_id từ URL: {}", url);
            return null;
        }
    }
}
