package com.example.machinesshop.controller.admin;

import com.example.machinesshop.dto.product.ProductDTO;
import com.example.machinesshop.dto.product.ProductDTORequestCreate;
import com.example.machinesshop.dto.product.ProductDTORequestUpdate;
import com.example.machinesshop.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Tag(name = "Product",description = "API dành cho admin sử dụng")
@Slf4j
public class ProductControllerAdmin {
    private final ProductService productService;
    @Operation(
            summary = "Cập nhật thông tin sản phẩm",
            description = "Cập nhật thông tin chi tiết của một sản phẩm dựa trên ID và dữ liệu truyền vào."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật sản phẩm thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm với ID tương ứng")
    })
    @PutMapping("{id}")
    public ResponseEntity<ProductDTO> UpdateProduct(
            @Parameter(description = "ID của sản phẩm cần cập nhật")
            @PathVariable Long id,
            @Parameter(description = "Dữ liệu cập nhật cho sản phẩm")
            @RequestBody ProductDTORequestUpdate product
    ) {
        return ResponseEntity.ok(productService.updateProducts(product, id));
    }

    @Operation(
            summary = "Xóa sản phẩm",
            description = "Xóa một sản phẩm khỏi hệ thống dựa trên ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xóa sản phẩm thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm với ID tương ứng")
    })
    @DeleteMapping("{id}")
    public ResponseEntity<String> DeleteProduct(
            @Parameter(description = "ID của sản phẩm cần xóa")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(productService.deleteProductById(id));
    }

    @Operation(
            summary = "Tạo mới sản phẩm",
            description = "Tạo mới một sản phẩm với thông tin chi tiết do admin cung cấp."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo sản phẩm mới thành công")
    })
    @PostMapping
    public ResponseEntity<ProductDTO> CreateProduct(
            @Parameter(description = "Thông tin sản phẩm cần tạo mới")
            @RequestBody ProductDTORequestCreate productDTORequestCreate
    ) {
        return ResponseEntity.ok(productService.createProduct(productDTORequestCreate));
    }

}
