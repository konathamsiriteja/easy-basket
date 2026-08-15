package com.easybasket.product.service;

import com.easybasket.product.dto.ProductRequest;
import com.easybasket.product.dto.ProductResponse;
import com.easybasket.product.entity.Product;
import com.easybasket.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product(
                request.getName(),
                request.getCategory(),
                request.getPrice(),
                request.getStock());

        Product saved = productRepository.save(product);

        return new ProductResponse(
                saved.getId(),
                saved.getName(),
                saved.getCategory(),
                saved.getPrice(),
                saved.getStock());
    }
}
