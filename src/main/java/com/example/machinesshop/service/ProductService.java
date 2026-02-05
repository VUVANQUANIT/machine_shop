package com.example.machinesshop.service;

import com.example.machinesshop.dto.product.*;
import com.example.machinesshop.ENUM.ProductStatus;
import com.example.machinesshop.entity.Product;
import com.example.machinesshop.entity.ProductImage;
import com.example.machinesshop.exception.ResourceNotFoundException;
import com.example.machinesshop.mappers.ProductMapper;
import com.example.machinesshop.repository.ProductImageRepository;
import com.example.machinesshop.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductMapper productMapper;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("price", "createdAt", "name");
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
    @Transactional
    public ProductDetailDTO getProductDetailById(Long id) {
        log.info("Find product by id {}", id);
        Product product = productRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy"));

        return productMapper.toDetailDTO(product);
    }
    @Transactional(readOnly = true)
    public List<ProductListDTO> getProducts() {
        log.info("Find all products");
        List<ProductListDTO> products = productRepository.findAllActive();
        if (products.isEmpty()) {
            log.error("No products found");
            throw new ResourceNotFoundException(String.format("Không có sản phẩm nào được tìm thấy"));
        }
        return products;
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
        Product product = productRepository.findDetailByIdAndActice(id)
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
        Page<Product> productPage = name == null || !StringUtils.hasText(name)
                ? productRepository.findAll(pageable)
                : productRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        List<ProductDTO> productDTOs = productPage.getContent().stream().map(productMapper::toResponseDTO).toList();
        return PageResponse.of(productDTOs, productPage);
    }

    /**
     * Tìm kiếm sản phẩm chuẩn production: keyword, categoryId, khoảng giá, phân trang, sắp xếp.
     * Chỉ trả về sản phẩm ACTIVE.
     */
    @Transactional(readOnly = true)
    public PageResponse<ProductListDTO> search(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                                int page, int size, String sort) {
        log.info("Search products: keyword={}, categoryId={}, minPrice={}, maxPrice={}, page={}, size={}, sort={}",
                keyword, categoryId, minPrice, maxPrice, page, size, sort);

        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"), ProductStatus.ACTIVE));

            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.trim().toLowerCase() + "%"));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("categoryId"), categoryId));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        List<Product> content = productPage.getContent();

        if (content.isEmpty()) {
            return PageResponse.of(List.of(), productPage);
        }

        Set<Long> productIds = content.stream().map(Product::getId).collect(Collectors.toSet());
        List<ProductImage> images = productImageRepository.findByProductIdIn(productIds);
        Map<Long, String> thumbnailByProductId = images.stream()
                .collect(Collectors.groupingBy(ProductImage::getProductId,
                        Collectors.collectingAndThen(Collectors.minBy(Comparator.comparing(ProductImage::getId)),
                                opt -> opt.map(ProductImage::getImageUrl).orElse(null))));

        List<ProductListDTO> dtos = content.stream()
                .map(p -> new ProductListDTO(p.getId(), p.getName(), p.getPrice(),
                        thumbnailByProductId.getOrDefault(p.getId(), null)))
                .toList();
        return PageResponse.of(dtos, productPage);
    }

    /** Parse sort param "field,asc" hoặc "field,desc". Chỉ chấp nhận field an toàn. Mặc định createdAt,desc. */
    private Sort parseSort(String sort) {
        if (!StringUtils.hasText(sort)) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String[] parts = sort.trim().split(",");
        String field = parts[0].trim();
        Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        if (!ALLOWED_SORT_FIELDS.contains(field)) {
            field = "createdAt";
        }
        return Sort.by(direction, field);
    }
}
