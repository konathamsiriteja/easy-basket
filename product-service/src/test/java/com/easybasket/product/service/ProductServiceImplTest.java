package com.easybasket.product.service;

import com.easybasket.product.dto.ProductRequest;
import com.easybasket.product.dto.ProductResponse;
import com.easybasket.product.entity.Product;
import com.easybasket.product.exception.ProductNotFoundException;
import com.easybasket.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        ProductRequest request = new ProductRequest("Wireless Mouse", "Electronics", new BigDecimal("19.99"), 100,
                "Ergonomic wireless mouse");

        Product saved = new Product("Wireless Mouse", "Electronics", new BigDecimal("19.99"), 100,
                "Ergonomic wireless mouse");
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
        assertThat(persisted.getDescription()).isEqualTo("Ergonomic wireless mouse");

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Wireless Mouse");
        assertThat(response.getCategory()).isEqualTo("Electronics");
        assertThat(response.getPrice()).isEqualByComparingTo("19.99");
        assertThat(response.getStock()).isEqualTo(100);
        assertThat(response.getDescription()).isEqualTo("Ergonomic wireless mouse");
        assertThat(response.getAvailability()).isEqualTo("IN_STOCK");
    }

    @Test
    void createProduct_withoutDescription_persistsNullDescription() {
        ProductRequest request = new ProductRequest("Widget", "Tools", new BigDecimal("5.00"), 1);
        Product saved = new Product("Widget", "Tools", new BigDecimal("5.00"), 1);
        saved.setId(3L);
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductResponse response = productService.createProduct(request);

        assertThat(response.getDescription()).isNull();
    }

    @Test
    void getProductById_found_mapsEntityAndDerivesInStockAvailability() {
        Product product = new Product("Wireless Mouse", "Electronics", new BigDecimal("19.99"), 100,
                "Ergonomic wireless mouse");
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDescription()).isEqualTo("Ergonomic wireless mouse");
        assertThat(response.getAvailability()).isEqualTo("IN_STOCK");
    }

    @Test
    void getProductById_found_zeroStock_derivesOutOfStockAvailability() {
        Product product = new Product("Widget", "Tools", new BigDecimal("5.00"), 0);
        product.setId(2L);
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById(2L);

        assertThat(response.getAvailability()).isEqualTo("OUT_OF_STOCK");
    }

    @Test
    void getProductById_notFound_throwsProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
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
