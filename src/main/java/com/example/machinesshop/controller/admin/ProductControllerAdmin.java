package com.example.machinesshop.controller.admin;

import com.example.machinesshop.dto.ApiResponse;
import com.example.machinesshop.dto.product.ProductDTO;
import com.example.machinesshop.dto.product.ProductDTORequestCreate;
import com.example.machinesshop.dto.product.ProductDTORequestUpdate;
import com.example.machinesshop.dto.product.SpecEntryDTO;
import com.example.machinesshop.service.ProductImageServiceImpl;
import com.example.machinesshop.service.ProductSpecificationService;
import com.example.machinesshop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Tag(name = "Product",description = "API dành cho admin sử dụng")
@Slf4j
public class ProductControllerAdmin {
    private final ProductService productService;
    private final ProductImageServiceImpl productImageService;
    private final ProductSpecificationService productSpecificationService;
    @Operation(
            summary = "Cập nhật thông tin sản phẩm",
            description = "Cập nhật thông tin chi tiết của một sản phẩm dựa trên ID và dữ liệu truyền vào."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật sản phẩm thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm với ID tương ứng")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> UpdateProduct(
            @Parameter(description = "ID của sản phẩm cần cập nhật") @PathVariable Long id,
            @Parameter(description = "Dữ liệu cập nhật cho sản phẩm") @RequestBody ProductDTORequestUpdate product
    ) {
        ProductDTO dto = productService.updateProducts(product, id);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật sản phẩm thành công.", dto));
    }

    @Operation(
            summary = "Xóa sản phẩm",
            description = "Xóa một sản phẩm khỏi hệ thống dựa trên ID."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Xóa sản phẩm thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm với ID tương ứng")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> DeleteProduct(
            @Parameter(description = "ID của sản phẩm cần xóa") @PathVariable Long id
    ) {
        String msg = productService.deleteProductById(id);
        return ResponseEntity.ok(ApiResponse.ok(msg, Map.of("id", id)));
    }

    @Operation(
            summary = "Tạo mới sản phẩm",
            description = "Tạo mới một sản phẩm với thông tin chi tiết do admin cung cấp."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo sản phẩm thành công, trả về chi tiết sản phẩm và thông báo"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductDTO>> CreateProduct(
            @Parameter(description = "Thông tin sản phẩm cần tạo mới") @RequestBody ProductDTORequestCreate productDTORequestCreate
    ) {
        ProductDTO created = productService.createProduct(productDTORequestCreate);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location)
                .body(ApiResponse.created("Tạo sản phẩm thành công.", created));
    }
    @Operation(summary = "Upload ảnh sản phẩm", description = "Tải lên một hoặc nhiều ảnh cho sản phẩm. Response chuẩn: success, message, data (productId, uploadedCount).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Upload thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "File vượt quá 10MB hoặc không hợp lệ")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/images")
    public ResponseEntity<ApiResponse<Map<String, Object>>> upload(
            @Parameter(description = "ID sản phẩm") @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) {
        for (MultipartFile file : files) {
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.<Map<String, Object>>builder()
                                .success(false)
                                .message("File vượt quá 10MB.")
                                .build());
            }
        }
        productImageService.uploadImages(id, files);
        return ResponseEntity.ok(ApiResponse.ok(
                "Upload ảnh thành công.",
                Map.of("productId", id, "uploadedCount", files.size())));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/specifications")
    @Operation(summary = "Thêm thông số kỹ thuật sản phẩm", description = "Thêm danh sách cặp specKey – specValue cho sản phẩm. Chi tiết sản phẩm (detail) sẽ trả về list specifications.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thêm thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm")
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> addSpecifications(
            @Parameter(description = "ID sản phẩm") @PathVariable Long id,
            @RequestBody @jakarta.validation.Valid List<SpecEntryDTO> specifications) {
        productSpecificationService.addSpecifications(id, specifications);
        return ResponseEntity.ok(ApiResponse.ok(
                "Thêm thông số kỹ thuật thành công.",
                Map.of("productId", id, "addedCount", specifications.size())));
    }
}
