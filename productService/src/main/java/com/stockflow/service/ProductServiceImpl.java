package com.stockflow.service;

import com.stockflow.model.Product;
import com.stockflow.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    public ProductServiceImpl(ProductRepository repo) {
        this.repo = repo;
    }

    @Override
    public Product addProduct(Product product) {
        // Check if product with same name and category already exists
        return repo.findByNameAndCategory(product.getName(), product.getCategory())
                .map(existing -> {
                    // Product exists, update quantity and price if different
                    existing.setQuantity(existing.getQuantity() + product.getQuantity());
                    // Update price per unit to the latest one
                    existing.setPricePerUnit(product.getPricePerUnit());
                    return repo.save(existing);
                })
                .orElseGet(() -> {
                    // Product doesn't exist, create new one
                    return repo.save(product);
                });
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> list = repo.findAll();
        // totalPrice is computed by getter, no change needed
        return list;
    }

    @Override
    public Product getProductById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public List<Product> getByCategory(String category) {
        return repo.findByCategory(category);
    }

    @Override
    public double getTotalInventoryValue() {
        return repo.findAll()
                   .stream()
                   .mapToDouble(Product::getTotalPrice)
                   .sum();
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        return repo.findById(id).map(existing -> {
            existing.setName(product.getName());
            existing.setCategory(product.getCategory());
            existing.setQuantity(product.getQuantity());
            existing.setPricePerUnit(product.getPricePerUnit());
            return repo.save(existing);
        }).orElse(null);
    }

    @Override
    public void deleteProduct(Long id) {
        // Exception handling
        if (!repo.existsById(id)) {
            throw new RuntimeException("Cannot delete: Product not found with id: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    public List<Product> getLowStockProducts(int threshold) {
        // More advanced Stream operations demonstrating lambda expressions
        return repo.findAll()
                   .stream()
                   .filter(product -> product.getQuantity() < threshold)
                   .sorted((p1, p2) -> Integer.compare(p1.getQuantity(), p2.getQuantity()))
                   .collect(Collectors.toList());
    }

    @Override
    public List<String> getDistinctCategories() {
        // Advanced Stream operations with distinct and method references
        return repo.findAll()
                   .stream()
                   .map(Product::getCategory)
                   .distinct()
                   .sorted()
                   .collect(Collectors.toList());
    }
}
