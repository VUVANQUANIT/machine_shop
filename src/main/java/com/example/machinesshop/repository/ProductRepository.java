package com.example.machinesshop.repository;

import com.example.machinesshop.dto.product.ProductListDTO;
import com.example.machinesshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

        Page<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);
        /**
         * Detail query with FETCH JOIN to avoid N+1 on associations.
         */
        @Query("""
            select distinct p
            from Product p
            left join fetch p.images
            left join fetch p.category
            where p.id = :id
                        and p.isActive = true
            """)
        Optional<Product> findDetailById(@Param("id") Long id);

        /**
         * All ACTIVE products for public list, optionally filtered by category.
         * Uses FETCH JOIN for category and images to avoid N+1 and get thumbnail.
         */
        @Query("""
            select distinct p
            from Product p
            left join fetch p.images
            left join fetch p.category
            where  (:categoryId is null or p.categoryId = :categoryId)
                           and p.isActive = true
            """)
        List<Product> findByCategoryActive(@Param("categoryId") Long categoryId);

        @Query("""
            select new com.example.machinesshop.dto.product.ProductListDTO(
                        p.id,
                        p.name,
                        p.price,
                        min(i.imageUrl)
            )
            from Product p
            left join ProductImage i on p.id = i.productId
            where  p.isActive = true
            group by p.id,p.name,p.description
            """)
        List<ProductListDTO> findAllActive();

        /**
         * Soft delete: set status = INACTIVE.
         */
        @Transactional
        @Modifying
        @Query("""
            update Product p
            set p.isActive = false 
            where p.id = :id and p.isActive = true
            """)
        int softDeleteById(@Param("id") Long id);
        @Query("""
            select p 
            from Product p 
            left join fetch p.images
            left join fetch p.category 
            where p.id = :id 
            and  p.isActive = true 
            """)
        Optional<Product> findDetailByIdActive(Long id);
}
