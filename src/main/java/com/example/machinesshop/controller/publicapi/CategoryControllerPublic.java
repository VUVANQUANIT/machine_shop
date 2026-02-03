package com.example.machinesshop.controller.publicapi;

import com.example.machinesshop.dto.category.CategoryDTO;
import com.example.machinesshop.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/categories")
@RequiredArgsConstructor
@Tag(name = "Category (Public)", description = "Danh sách loại sản phẩm – dùng cho filter/dropdown phía client")
public class CategoryControllerPublic {
    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Danh sách loại sản phẩm", description = "Lấy tất cả category (không cần đăng nhập), dùng cho filter hoặc dropdown.")
    public ResponseEntity<List<CategoryDTO>> list() {
        return ResponseEntity.ok(categoryService.findAll());
    }
}
