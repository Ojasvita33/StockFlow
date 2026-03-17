package com.stockflow.controller;

import com.stockflow.dto.ProductRequest;
import com.stockflow.dto.ProductResponse;
import com.stockflow.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // Reads companyId injected by JwtAuthFilter
    private Long getCompanyId(HttpServletRequest req) {
        Object id = req.getAttribute("companyId");
        if (id == null) throw new RuntimeException("Company context missing from token");
        return (Long) id;
    }

    // ── WRITE — Admin only ────────────────────────────

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest request,
                                                       HttpServletRequest req) {
        return ResponseEntity.ok(service.addProduct(request, getCompanyId(req)));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id,
                                                   @RequestBody ProductRequest request,
                                                   HttpServletRequest req) {
        ProductResponse updated = service.updateProduct(id, request, getCompanyId(req));
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest req) {
        service.deleteProduct(id, getCompanyId(req));
        return ResponseEntity.noContent().build();
    }

    // ── READ — All authenticated roles ───────────────

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll(HttpServletRequest req) {
        return ResponseEntity.ok(service.getAllProducts(getCompanyId(req)));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<ProductResponse>> getProducts(Pageable pageable,
                                                              HttpServletRequest req) {
        return ResponseEntity.ok(service.getProducts(getCompanyId(req), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id,
                                                    HttpServletRequest req) {
        ProductResponse p = service.getProductById(id, getCompanyId(req));
        return p != null ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getByCategory(@PathVariable String category,
                                                                HttpServletRequest req) {
        return ResponseEntity.ok(service.getByCategory(category, getCompanyId(req)));
    }

    @GetMapping("/value/total")
    public ResponseEntity<Map<String, Double>> getTotalValue(HttpServletRequest req) {
        return ResponseEntity.ok(Map.of("totalInventoryValue",
                service.getTotalInventoryValue(getCompanyId(req))));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponse>> getLowStock(
            @RequestParam(defaultValue = "5") int threshold,
            HttpServletRequest req) {
        return ResponseEntity.ok(service.getLowStockProducts(threshold, getCompanyId(req)));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories(HttpServletRequest req) {
        return ResponseEntity.ok(service.getDistinctCategories(getCompanyId(req)));
    }
}
