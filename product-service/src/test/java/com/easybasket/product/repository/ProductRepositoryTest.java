package com.easybasket.product.repository;

import com.easybasket.product.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void save_generatesIdentityId() {
        Product product = new Product("Wireless Mouse", "Electronics", new BigDecimal("19.99"), 100);

        Product saved = productRepository.save(product);

        assertThat(saved.getId()).isNotNull();
        Optional<Product> found = productRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Wireless Mouse");
    }

    @Test
    void save_persistsTwoDecimalPricePrecision() {
        Product product = new Product("Widget", "Tools", new BigDecimal("12.34"), 10);

        Product saved = productRepository.save(product);

        Product found = productRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getPrice()).isEqualByComparingTo("12.34");
    }

    @Test
    void save_zeroStockBoundary_isPersisted() {
        Product product = new Product("Gadget", "Electronics", new BigDecimal("9.99"), 0);

        Product saved = productRepository.save(product);

        Product found = productRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getStock()).isZero();
    }

    @Test
    void save_multipleProducts_generatesUniqueIds() {
        Product first = productRepository.save(new Product("Item A", "Category A", new BigDecimal("1.00"), 1));
        Product second = productRepository.save(new Product("Item B", "Category B", new BigDecimal("2.00"), 2));

        assertThat(first.getId()).isNotEqualTo(second.getId());
        List<Product> all = productRepository.findAll();
        assertThat(all).hasSize(2);
    }
}
