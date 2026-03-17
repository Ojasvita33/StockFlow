package com.stockflow.service;

import com.stockflow.dto.ProductRequest;
import com.stockflow.dto.ProductResponse;
import com.stockflow.model.Product;
import com.stockflow.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ProductResponse addProduct(ProductRequest request, Long companyId) {
        Product product = new Product();
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setQuantity(request.getQuantity());
        product.setPricePerUnit(request.getPricePerUnit());
        product.setCompanyId(companyId);

        // If same name+category exists for this company, restock instead of duplicate
        Product saved = repo.findByNameAndCategoryAndCompanyId(
                product.getName(), product.getCategory(), companyId)
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + product.getQuantity());
                    existing.setPricePerUnit(product.getPricePerUnit());
                    return repo.save(existing);
                })
                .orElseGet(() -> repo.save(product));

        return mapToResponse(saved);
    }

    @Override
    public List<ProductResponse> getAllProducts(Long companyId) {
        return repo.findByCompanyId(companyId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public Page<ProductResponse> getProducts(Long companyId, Pageable pageable) {
        return repo.findByCompanyId(companyId, pageable).map(this::mapToResponse);
    }

    @Override
    public ProductResponse getProductById(Long id, Long companyId) {
        return repo.findById(id)
                .filter(p -> p.getCompanyId().equals(companyId))
                .map(this::mapToResponse)
                .orElse(null);
    }

    @Override
    public List<ProductResponse> getByCategory(String category, Long companyId) {
        return repo.findByCompanyIdAndCategory(companyId, category)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public double getTotalInventoryValue(Long companyId) {
        return repo.findByCompanyId(companyId)
                .stream().mapToDouble(Product::getTotalPrice).sum();
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request, Long companyId) {
        return repo.findById(id)
                .filter(p -> p.getCompanyId().equals(companyId))
                .map(existing -> {
                    existing.setName(request.getName());
                    existing.setCategory(request.getCategory());
                    existing.setQuantity(request.getQuantity());
                    existing.setPricePerUnit(request.getPricePerUnit());
                    return mapToResponse(repo.save(existing));
                }).orElse(null);
    }

    @Override
    public void deleteProduct(Long id, Long companyId) {
        Product product = repo.findById(id)
                .filter(p -> p.getCompanyId().equals(companyId))
                .orElseThrow(() -> new RuntimeException("Product not found or access denied"));
        repo.delete(product);
    }

    @Override
    public List<ProductResponse> getLowStockProducts(int threshold, Long companyId) {
        return repo.findByCompanyId(companyId).stream()
                .filter(p -> p.getQuantity() < threshold)
                .sorted((a, b) -> Integer.compare(a.getQuantity(), b.getQuantity()))
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<String> getDistinctCategories(Long companyId) {
        return repo.findByCompanyId(companyId).stream()
                .map(Product::getCategory).distinct().sorted().collect(Collectors.toList());
    }

    private ProductResponse mapToResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getCategory(),
                p.getQuantity(), p.getPricePerUnit(), p.getTotalPrice());
    }
}
