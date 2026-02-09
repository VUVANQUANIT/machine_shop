# Image Storage Architecture (SOLID)

## Cấu trúc

```
ImageStorageService (Interface)
    ├── CloudinaryImageStorageService (Cloudinary implementation)
    └── LocalImageStorageService (Local filesystem fallback)
```

## SOLID Principles Applied

### ✅ Single Responsibility Principle (SRP)
- `ImageStorageService`: Chỉ định nghĩa contract cho upload/delete
- `CloudinaryImageStorageService`: Chỉ lo upload lên Cloudinary
- `LocalImageStorageService`: Chỉ lo lưu local filesystem
- `ProductImageServiceImpl`: Chỉ lo business logic (validate product, save to DB)

### ✅ Open/Closed Principle (OCP)
- Có thể thêm implementation mới (S3, Azure Blob...) mà không sửa code cũ
- Chỉ cần implement `ImageStorageService` và đánh dấu `@Service`

### ✅ Liskov Substitution Principle (LSP)
- Bất kỳ implementation nào của `ImageStorageService` đều có thể thay thế nhau
- `ProductImageServiceImpl` không cần biết đang dùng Cloudinary hay Local

### ✅ Interface Segregation Principle (ISP)
- Interface nhỏ, focused: chỉ 2 methods (`uploadImage`, `deleteImage`)
- Không force implementation phải implement methods không cần thiết

### ✅ Dependency Inversion Principle (DIP)
- `ProductImageServiceImpl` phụ thuộc vào **interface** `ImageStorageService`
- Không phụ thuộc vào concrete class (`CloudinaryImageStorageService` hay `LocalImageStorageService`)
- Spring tự động inject implementation phù hợp

## Cách hoạt động

### Khi có Cloudinary config:
1. `CloudinaryConfig` tạo `Cloudinary` bean
2. `CloudinaryImageStorageService` được tạo (có `@ConditionalOnBean(Cloudinary.class)`)
3. `LocalImageStorageService` **không** được tạo (có `@ConditionalOnMissingBean`)
4. Spring inject `CloudinaryImageStorageService` vào `ProductImageServiceImpl`

### Khi không có Cloudinary config:
1. `CloudinaryConfig` **không** tạo bean
2. `CloudinaryImageStorageService` **không** được tạo
3. `LocalImageStorageService` được tạo (fallback)
4. Spring inject `LocalImageStorageService` vào `ProductImageServiceImpl`

## Cấu hình

### Cloudinary (Production)
Thêm vào `.env` hoặc environment variables:
```properties
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

### Local (Development - fallback)
Nếu không có Cloudinary config, tự động dùng local storage.
Files lưu tại: `uploads/products/` (có thể config qua `app.upload.directory`)

## Sử dụng

```java
@Autowired
private ProductImageService productImageService; // Inject interface

// Upload images - tự động dùng Cloudinary nếu có config, không thì dùng Local
productImageService.uploadImages(productId, files);
```

## Thêm Storage Provider mới

1. Implement `ImageStorageService`
2. Đánh dấu `@Service`
3. (Optional) Thêm `@ConditionalOn...` nếu cần điều kiện
4. Spring tự động inject vào `ProductImageServiceImpl`

Example:
```java
@Service
@ConditionalOnProperty("storage.type=s3")
public class S3ImageStorageService implements ImageStorageService {
    // ...
}
```
