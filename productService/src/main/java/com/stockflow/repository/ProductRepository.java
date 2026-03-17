package com.stockflow.repository;

import com.stockflow.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // All queries scoped to companyId
    List<Product>    findByCompanyId(Long companyId);
    Page<Product>    findByCompanyId(Long companyId, Pageable pageable);
    List<Product>    findByCompanyIdAndCategory(Long companyId, String category);
    Optional<Product> findByNameAndCategoryAndCompanyId(String name, String category, Long companyId);
    boolean          existsByIdAndCompanyId(Long id, Long companyId);
}
