package com.stockflow.controller;

import com.stockflow.model.Product;
import com.stockflow.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller demonstrating Spring MVC concepts
 * - RESTful API design
 * - HTTP methods (GET, POST, PUT, DELETE)
 * - Path variables and request parameters
 * - Response entities and status codes
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    // Constructor injection (Dependency Injection)
    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product saved = service.addProduct(product);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(service.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        Product p = service.getProductById(id);
        return p != null ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(service.getByCategory(category));
    }

    @GetMapping("/value/total")
    public ResponseEntity<Map<String, Double>> getTotalValue() {
        double total = service.getTotalInventoryValue();
        return ResponseEntity.ok(Map.of("totalInventoryValue", total));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        Product updated = service.updateProduct(id, product);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Additional endpoints demonstrating Java concepts
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts(@RequestParam(defaultValue = "5") int threshold) {
        return ResponseEntity.ok(service.getLowStockProducts(threshold));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(service.getDistinctCategories());
    }
}
