package com.stockflow.service;

import com.stockflow.model.Product;

import java.util.List;

/**
 * ProductService interface demonstrating Java concepts:
 * - Interface definition
 * - Method signatures
 * - Collections usage
 */
public interface ProductService {
    Product addProduct(Product product);
    List<Product> getAllProducts();
    Product getProductById(Long id);
    List<Product> getByCategory(String category);
    double getTotalInventoryValue();
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
    
    // Additional methods for Java concepts demonstration
    List<Product> getLowStockProducts(int threshold);
    List<String> getDistinctCategories();
}
