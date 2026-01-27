package com.example.machinesshop.controller.publicapi;

import com.example.machinesshop.dto.ProductDTO;
import com.example.machinesshop.repository.ProductRepository;
import com.example.machinesshop.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/public/products")
@RequiredArgsConstructor
@Tag(name = "Product",description = "API công khai cho người dùng xem sản phẩm")
@Slf4j
public class ProductControllerPublic {
    private final ProductService productService;
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable("id")Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    @GetMapping()
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getProducts());
    }
}
