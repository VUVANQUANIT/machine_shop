package com.example.machinesshop.controller.admin;

import com.example.machinesshop.dto.ApiResponse;
import com.example.machinesshop.dto.category.CategoryDTO;
import com.example.machinesshop.dto.category.CategoryDTORequestCreate;
import com.example.machinesshop.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@Tag(name = "Category (Admin)", description = "Quản lý loại sản phẩm – thêm mới, danh sách (dropdown)")
public class CategoryControllerAdmin {
    private final CategoryService categoryService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Danh sách loại sản phẩm", description = "Lấy tất cả category cho dropdown khi tạo/sửa sản phẩm.")
    @ApiResponses(value = { @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công") })
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> list() {
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách loại sản phẩm thành công.", categoryService.findAll()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Thêm loại sản phẩm", description = "Tạo mới một loại sản phẩm (category). Response: success, message, data (category vừa tạo).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo thành công, trả về chi tiết category và thông báo"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc tên đã tồn tại")
    })
    public ResponseEntity<ApiResponse<CategoryDTO>> create(@Valid @RequestBody CategoryDTORequestCreate request) {
        CategoryDTO created = categoryService.create(request);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location)
                .body(ApiResponse.created("Tạo loại sản phẩm thành công.", created));
    }
}
