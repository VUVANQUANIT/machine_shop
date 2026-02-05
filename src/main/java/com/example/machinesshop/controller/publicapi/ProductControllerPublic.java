package com.example.machinesshop.controller.publicapi;

import com.example.machinesshop.dto.product.ProductDTO;
import com.example.machinesshop.dto.product.PageResponse;
import com.example.machinesshop.dto.product.ProductDetailDTO;
import com.example.machinesshop.dto.product.ProductListDTO;
import com.example.machinesshop.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/public/products")
@RequiredArgsConstructor
@Tag(name = "Product",description = "API công khai cho người dùng xem sản phẩm")
@Slf4j
public class ProductControllerPublic {
    private final ProductService productService;
    @Operation(
            summary = "Lấy chi tiết sản phẩm theo ID",
            description = "Trả về thông tin chi tiết của một sản phẩm dựa trên ID truyền vào."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy thông tin sản phẩm thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm với ID tương ứng")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "ID của sản phẩm cần lấy thông tin")
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(
            summary = "Danh sách tất cả sản phẩm",
            description = "Trả về danh sách tất cả sản phẩm đang được bán."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách sản phẩm thành công")
    })
    @GetMapping()
    public ResponseEntity<List<ProductListDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getProducts());
    }

    @Operation(
            summary = "Tìm kiếm & lọc sản phẩm (production)",
            description = "Tìm theo từ khóa, lọc theo loại sản phẩm và khoảng giá, phân trang, sắp xếp. Chỉ trả về sản phẩm ACTIVE."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductListDTO>> search(
            @Parameter(description = "Từ khóa tìm trong tên sản phẩm") @RequestParam(required = false) String keyword,
            @Parameter(description = "Lọc theo ID loại sản phẩm (category)") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Giá tối thiểu (VNĐ)") @RequestParam(required = false) java.math.BigDecimal minPrice,
            @Parameter(description = "Giá tối đa (VNĐ)") @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @Parameter(description = "Trang (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số phần tử mỗi trang") @RequestParam(defaultValue = "12") int size,
            @Parameter(description = "Sắp xếp: field,direction. Ví dụ price,asc hoặc createdAt,desc. Field cho phép: price, createdAt, name") @RequestParam(required = false) String sort
    ) {
        return ResponseEntity.ok(productService.search(keyword, categoryId, minPrice, maxPrice, page, size, sort));
    }
    @GetMapping("/detail/{id}")
    public ResponseEntity<ProductDetailDTO>  getProductDetailById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductDetailById(id));
    }
}
