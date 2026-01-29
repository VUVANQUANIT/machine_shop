package com.example.machinesshop.service;

import com.example.machinesshop.dto.product.ProductDTO;
import com.example.machinesshop.dto.product.ProductDTORequestCreate;
import com.example.machinesshop.dto.product.ProductDTORequestUpdate;
import com.example.machinesshop.dto.product.PageResponse;
import com.example.machinesshop.entity.Product;
import com.example.machinesshop.exception.ResourceNotFoundException;
import com.example.machinesshop.mappers.ProductMapper;
import com.example.machinesshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        log.info("Find product by id {}", id);
        Optional<Product> product = productRepository.findDetailById(id);
        if (!product.isPresent()) {
            log.error("Product not found {}", id);
            throw new ResourceNotFoundException(String.format("Không tìm thấy sản phẩm với ID: %d ",id));
        }
        ProductDTO productDTO = productMapper.toResponseDTO(product.get());
        return productDTO;
    }
    @Transactional(readOnly = true)
    public List<ProductDTO> getProducts() {
        log.info("Find all products");
        List<Product> products = productRepository.findAllActive();
        if (products.isEmpty()) {
            log.error("No products found");
            throw new ResourceNotFoundException(String.format("Không có sản phẩm nào được tìm thấy"));
        }
        return products.stream().map(productMapper::toResponseDTO).toList();
    }
    @Transactional
    public ProductDTO updateProducts(ProductDTORequestUpdate  productDTORequestUpdate,Long id) {
        log.info("Updating product with id {}", id);

        // Tìm product với proper error handling
        Product product = productRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Không tìm thấy sản phẩm phầm với id = %d", id)));

        // Update chỉ những field không null
        productMapper.updateProductFromDTO(product, productDTORequestUpdate);

        // Lưu và trả về
        Product savedProduct = productRepository.save(product);
        log.info("Product updated successfully: {}", savedProduct.getId());

        return productMapper.toResponseDTO(savedProduct);
    }
    @Transactional
    public String deleteProductById(Long id) {
        log.info("Deleting product with id {}", id);

        // Tìm product với proper error handling
        Product product = productRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Không tìm thấy sản phẩm phầm với id = %d", id)));
        int idDelete = productRepository.softDeleteById(id);
        log.info("Product deleted successfully");
       return "Xoá thành công sản phẩm";
    }
    @Transactional
    public ProductDTO createProduct(ProductDTORequestCreate productDTORequestCreate) {
        return productMapper.toResponseDTO(productRepository.save(productMapper.toEntity(productDTORequestCreate)));
    }
    @Transactional(readOnly = true)
    public PageResponse<ProductDTO> searchByName(String name, int page, int size) {
        log.info("Find all products by name {}", name);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Product> productPage;
        if(name ==  null) {
            productPage = productRepository.findAll(pageable);
        }
        else {
            productPage = productRepository.findByNameContainingIgnoreCase(name,pageable);
        }
        List<ProductDTO> productDTOs = productPage.getContent().stream().map(productMapper::toResponseDTO).toList();
        return PageResponse.of(productDTOs,productPage);

    }

}
