package com.stockflow.controller;

import com.stockflow.dto.ProductRequest;
import com.stockflow.dto.ProductResponse;
import com.stockflow.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * REST Controller demonstrating Spring MVC concepts
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    // Constructor Injection
    public ProductController(ProductService service) {
        this.service = service;
    }

    // CREATE PRODUCT
    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest request) {
        return ResponseEntity.ok(service.addProduct(request));
    }

    // GET ALL PRODUCTS
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(service.getAllProducts());
    }

    // GET PRODUCT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        ProductResponse p = service.getProductById(id);
        return p != null ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
    }

    // GET PRODUCTS BY CATEGORY
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(service.getByCategory(category));
    }

    // TOTAL INVENTORY VALUE
    @GetMapping("/value/total")
    public ResponseEntity<Map<String, Double>> getTotalValue() {
        double total = service.getTotalInventoryValue();
        return ResponseEntity.ok(Map.of("totalInventoryValue", total));
    }

    // UPDATE PRODUCT
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id,
            @RequestBody ProductRequest request) {
        ProductResponse updated = service.updateProduct(id, request);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // DELETE PRODUCT
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // LOW STOCK PRODUCTS
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponse>> getLowStockProducts(
            @RequestParam(defaultValue = "5") int threshold) {
        return ResponseEntity.ok(service.getLowStockProducts(threshold));
    }

    // DISTINCT CATEGORIES
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(service.getDistinctCategories());
    }

    @GetMapping("/page")
    public ResponseEntity<Page<ProductResponse>> getProducts(Pageable pageable) {
        return ResponseEntity.ok(service.getProducts(pageable));
    }
}