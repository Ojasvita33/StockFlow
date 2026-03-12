package com.stockflow.service;

import com.stockflow.dto.ProductRequest;
import com.stockflow.dto.ProductResponse;
import com.stockflow.model.Product;
import com.stockflow.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    public ProductServiceImpl(ProductRepository repo) {
        this.repo = repo;
    }

    // ADD PRODUCT
    @Override
    public ProductResponse addProduct(ProductRequest request) {

        Product product = new Product();
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setQuantity(request.getQuantity());
        product.setPricePerUnit(request.getPricePerUnit());

        Product saved = repo.findByNameAndCategory(product.getName(), product.getCategory())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + product.getQuantity());
                    existing.setPricePerUnit(product.getPricePerUnit());
                    return repo.save(existing);
                })
                .orElseGet(() -> repo.save(product));

        return mapToResponse(saved);
    }

    // GET ALL PRODUCTS
    @Override
    public List<ProductResponse> getAllProducts() {
        return repo.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // GET PRODUCT BY ID
    @Override
    public ProductResponse getProductById(Long id) {
        return repo.findById(id)
                .map(this::mapToResponse)
                .orElse(null);
    }

    // GET BY CATEGORY
    @Override
    public List<ProductResponse> getByCategory(String category) {
        return repo.findByCategory(category)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // TOTAL INVENTORY VALUE
    @Override
    public double getTotalInventoryValue() {
        return repo.findAll()
                .stream()
                .mapToDouble(Product::getTotalPrice)
                .sum();
    }

    // UPDATE PRODUCT
    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product updated = repo.findById(id).map(existing -> {

            existing.setName(request.getName());
            existing.setCategory(request.getCategory());
            existing.setQuantity(request.getQuantity());
            existing.setPricePerUnit(request.getPricePerUnit());

            return repo.save(existing);

        }).orElse(null);

        return updated != null ? mapToResponse(updated) : null;
    }

    // DELETE PRODUCT
    @Override
    public void deleteProduct(Long id) {

        if (!repo.existsById(id)) {
            throw new RuntimeException("Cannot delete: Product not found with id: " + id);
        }

        repo.deleteById(id);
    }

    // LOW STOCK PRODUCTS
    @Override
    public List<ProductResponse> getLowStockProducts(int threshold) {

        return repo.findAll()
                .stream()
                .filter(product -> product.getQuantity() < threshold)
                .sorted((p1, p2) -> Integer.compare(p1.getQuantity(), p2.getQuantity()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // DISTINCT CATEGORIES
    @Override
    public List<String> getDistinctCategories() {

        return repo.findAll()
                .stream()
                .map(Product::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // ENTITY → DTO
    private ProductResponse mapToResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getQuantity(),
                product.getPricePerUnit(),
                product.getTotalPrice());
    }

    @Override
    public Page<ProductResponse> getProducts(Pageable pageable) {

        return repo.findAll(pageable)
                .map(this::mapToResponse);
    }
}