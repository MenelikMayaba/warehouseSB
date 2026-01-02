package com.aCompany.wms.service;

import com.aCompany.wms.model.Product;
import com.aCompany.wms.exceptions.ResourceNotFoundException;
import com.aCompany.wms.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product getProductById(Long id) throws ResourceNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Product getProductBySku(String sku) throws ResourceNotFoundException {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
    }

    @Transactional
    public Product createProduct(Product product) {
        if (productRepository.existsBySku(product.getSku())) {
            throw new IllegalArgumentException("Product with SKU " + product.getSku() + " already exists");
        }
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails) throws ResourceNotFoundException {
        Product product = getProductById(id);

        if (!product.getSku().equals(productDetails.getSku()) &&
                productRepository.existsBySku(productDetails.getSku())) {
            throw new IllegalArgumentException("Product with SKU " + productDetails.getSku() + " already exists");
        }

        product.setSku(productDetails.getSku());
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) throws ResourceNotFoundException {
        Product product = getProductById(id);
        // Add validation if product is in use
        productRepository.delete(product);
    }

    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(query, query);
    }
}
