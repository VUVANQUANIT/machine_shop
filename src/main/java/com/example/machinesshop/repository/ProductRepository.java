package com.example.machinesshop.repository;

import com.example.machinesshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

        Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
        /**
         * Detail query with FETCH JOIN to avoid N+1 on associations.
         */
        @Query("""
            select distinct p
            from Product p
            left join fetch p.images
            left join fetch p.category
            where p.id = :id
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
            where p.status = com.example.machinesshop.ENUM.ProductStatus.ACTIVE
              and (:categoryId is null or p.categoryId = :categoryId)
            """)
        List<Product> findByCategoryActive(@Param("categoryId") Long categoryId);

        @Query("""
            select distinct p
            from Product p
            left join fetch p.images
            left join fetch p.category
            where p.status = com.example.machinesshop.ENUM.ProductStatus.ACTIVE
            """)
        List<Product> findAllActive();

        /**
         * Soft delete: set status = INACTIVE.
         */
        @Transactional
        @Modifying
        @Query("""
            update Product p
            set p.status = com.example.machinesshop.ENUM.ProductStatus.INACTIVE
            where p.id = :id
            """)
        int softDeleteById(@Param("id") Long id);
        @Query("""
            select p 
            from Product p 
            left join fetch p.images
            left join fetch p.category 
            where p.id = :id 
            and p.status = com.example.machinesshop.ENUM.ProductStatus.ACTIVE
            """)
        Optional<Product> findDetailByIdAndActice(Long id);
}
