package com.example.machinesshop.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interface cho image storage service theo SOLID - Dependency Inversion Principle.
 * Cho phép thay đổi implementation (Cloudinary, Local, S3...) mà không sửa code business logic.
 */
public interface ImageStorageService {

    /**
     * Upload một file và trả về public URL.
     *
     * @param file File cần upload
     * @param folder Folder trong storage (ví dụ: "products", "categories")
     * @return Public URL của file đã upload
     * @throws ImageStorageException nếu upload thất bại
     */
    String uploadImage(MultipartFile file, String folder);

    /**
     * Xóa image từ storage (optional - có thể không implement nếu storage không hỗ trợ).
     *
     * @param imageUrl URL của image cần xóa
     * @return true nếu xóa thành công
     */
    default boolean deleteImage(String imageUrl) {
        // Default: không hỗ trợ delete (một số storage như Cloudinary free tier không có delete API)
        return false;
    }
}
