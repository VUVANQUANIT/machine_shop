package com.example.machinesshop.repository;

import com.example.machinesshop.entity.ProductSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSpeccificationRepository extends JpaRepository<ProductSpecification,Long> {
}
