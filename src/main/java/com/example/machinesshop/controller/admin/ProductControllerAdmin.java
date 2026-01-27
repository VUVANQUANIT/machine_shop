package com.example.machinesshop.controller.admin;

import com.example.machinesshop.dto.ProductDTO;
import com.example.machinesshop.dto.ProductDTORequestCreate;
import com.example.machinesshop.dto.ProductDTORequestUpdate;
import com.example.machinesshop.entity.Product;
import com.example.machinesshop.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Tag(name = "Product",description = "API dành cho admin sử dụng")
@Slf4j
public class ProductControllerAdmin {
    private final ProductService productService;
    @PutMapping("{id}")
    public ResponseEntity<ProductDTO> UpdateProduct(@PathVariable Long id, @RequestBody ProductDTORequestUpdate product) {
        return ResponseEntity.ok(productService.updateProducts(product, id));
    }
    @DeleteMapping("{id}")
    public ResponseEntity<String> DeleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.deleteProductById(id));
    }
    @PostMapping
    public ResponseEntity<ProductDTO> CreateProduct(@RequestBody ProductDTORequestCreate productDTORequestCreate) {
        return ResponseEntity.ok(productService.createProduct(productDTORequestCreate));
    }

}
