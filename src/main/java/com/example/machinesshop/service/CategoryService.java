package com.example.machinesshop.service;

import com.example.machinesshop.dto.category.CategoryDTO;
import com.example.machinesshop.dto.category.CategoryDTORequestCreate;
import com.example.machinesshop.entity.Category;
import com.example.machinesshop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDTO create(CategoryDTORequestCreate request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new IllegalArgumentException("Loại sản phẩm đã tồn tại: " + request.getName());
        }
        Category category = new Category();
        category.setName(request.getName().trim());
        category = categoryRepository.save(category);
        log.info("Category created: id={}, name={}", category.getId(), category.getName());
        return toDTO(category);
    }

    private CategoryDTO toDTO(Category c) {
        return new CategoryDTO(c.getId(), c.getName());
    }
}
