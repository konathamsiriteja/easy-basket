package com.easybasket.product.service;

import com.easybasket.product.dto.ProductRequest;
import com.easybasket.product.dto.ProductResponse;
import com.easybasket.product.entity.Product;
import com.easybasket.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository);
    }

    @Test
    void createProduct_mapsRequestToEntity_savesAndReturnsResponse() {
        ProductRequest request = new ProductRequest("Wireless Mouse", "Electronics", new BigDecimal("19.99"), 100);

        Product saved = new Product("Wireless Mouse", "Electronics", new BigDecimal("19.99"), 100);
        saved.setId(1L);
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductResponse response = productService.createProduct(request);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        Product persisted = captor.getValue();
        assertThat(persisted.getId()).isNull();
        assertThat(persisted.getName()).isEqualTo("Wireless Mouse");
        assertThat(persisted.getCategory()).isEqualTo("Electronics");
        assertThat(persisted.getPrice()).isEqualByComparingTo("19.99");
        assertThat(persisted.getStock()).isEqualTo(100);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Wireless Mouse");
        assertThat(response.getCategory()).isEqualTo("Electronics");
        assertThat(response.getPrice()).isEqualByComparingTo("19.99");
        assertThat(response.getStock()).isEqualTo(100);
    }

    @Test
    void createProduct_stockZero_isPersistedAsValidBoundary() {
        ProductRequest request = new ProductRequest("Widget", "Tools", new BigDecimal("5.00"), 0);
        Product saved = new Product("Widget", "Tools", new BigDecimal("5.00"), 0);
        saved.setId(2L);
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductResponse response = productService.createProduct(request);

        assertThat(response.getStock()).isZero();
    }
}
