package com.example.machinesshop.controller.publicapi;

import com.example.machinesshop.dto.product.ProductDTO;
import com.example.machinesshop.dto.product.PageResponse;
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
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getProducts());
    }

    @Operation(
            summary = "Tìm kiếm sản phẩm theo tên",
            description = "Tìm kiếm sản phẩm theo tên (không bắt buộc), có phân trang theo `page` và `size`."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm kiếm sản phẩm thành công")
    })
    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductDTO>>  searchProducts(
            @Parameter(description = "Tên sản phẩm cần tìm kiếm (có thể bỏ trống để lấy tất cả)")
            @RequestParam(required = false) String name,
            @Parameter(description = "Trang hiện tại, bắt đầu từ 0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng phần tử mỗi trang")
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ProductDTO> result = productService.searchByName(name,page,size);
        return ResponseEntity.ok(result);
    }
}
