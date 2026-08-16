package com.easybasket.product.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    void noArgsConstructor_allowsSettersToPopulateFields() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Wireless Mouse");
        product.setCategory("Electronics");
        product.setPrice(new BigDecimal("19.99"));
        product.setStock(100);

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Wireless Mouse");
        assertThat(product.getCategory()).isEqualTo("Electronics");
        assertThat(product.getPrice()).isEqualByComparingTo("19.99");
        assertThat(product.getStock()).isEqualTo(100);
    }

    @Test
    void equals_sameInstance_isTrue() {
        Product product = new Product("Widget", "Tools", BigDecimal.ONE, 1);
        product.setId(1L);

        assertThat(product).isEqualTo(product);
    }

    @Test
    void equals_null_isFalse() {
        Product product = new Product("Widget", "Tools", BigDecimal.ONE, 1);
        product.setId(1L);

        assertThat(product).isNotEqualTo(null);
    }

    @Test
    void equals_differentType_isFalse() {
        Product product = new Product("Widget", "Tools", BigDecimal.ONE, 1);
        product.setId(1L);

        assertThat(product).isNotEqualTo("not a product");
    }

    @Test
    void equals_sameId_isTrue_andHashCodeMatches() {
        Product first = new Product("Widget", "Tools", BigDecimal.ONE, 1);
        first.setId(1L);
        Product second = new Product("Different Name", "Different Category", BigDecimal.TEN, 5);
        second.setId(1L);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void equals_differentId_isFalse() {
        Product first = new Product("Widget", "Tools", BigDecimal.ONE, 1);
        first.setId(1L);
        Product second = new Product("Widget", "Tools", BigDecimal.ONE, 1);
        second.setId(2L);

        assertThat(first).isNotEqualTo(second);
    }
}
