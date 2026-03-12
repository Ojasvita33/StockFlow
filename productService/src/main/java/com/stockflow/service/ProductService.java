package com.stockflow.service;

import com.stockflow.dto.ProductRequest;
import com.stockflow.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * ProductService interface demonstrating Java concepts:
 * - Interface definition
 * - Method signatures
 * - Collections usage
 */
public interface ProductService {

    ProductResponse addProduct(ProductRequest request);

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Long id);

    List<ProductResponse> getByCategory(String category);

    double getTotalInventoryValue();

    Page<ProductResponse> getProducts(Pageable pageable);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);

    // Additional methods for Java concepts demonstration
    List<ProductResponse> getLowStockProducts(int threshold);

    List<String> getDistinctCategories();
}