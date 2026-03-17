package com.stockflow.service;

import com.stockflow.dto.ProductRequest;
import com.stockflow.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductResponse  addProduct(ProductRequest request, Long companyId);
    List<ProductResponse> getAllProducts(Long companyId);
    Page<ProductResponse> getProducts(Long companyId, Pageable pageable);
    ProductResponse  getProductById(Long id, Long companyId);
    List<ProductResponse> getByCategory(String category, Long companyId);
    double           getTotalInventoryValue(Long companyId);
    ProductResponse  updateProduct(Long id, ProductRequest request, Long companyId);
    void             deleteProduct(Long id, Long companyId);
    List<ProductResponse> getLowStockProducts(int threshold, Long companyId);
    List<String>     getDistinctCategories(Long companyId);
}
