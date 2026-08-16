package com.easybasket.product.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductResponseTest {

    @Test
    void noArgsConstructor_allowsSettersToPopulateFields() {
        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setName("Wireless Mouse");
        response.setCategory("Electronics");
        response.setPrice(new BigDecimal("19.99"));
        response.setStock(100);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Wireless Mouse");
        assertThat(response.getCategory()).isEqualTo("Electronics");
        assertThat(response.getPrice()).isEqualByComparingTo("19.99");
        assertThat(response.getStock()).isEqualTo(100);
    }

    @Test
    void allArgsConstructor_populatesFields() {
        ProductResponse response = new ProductResponse(2L, "Widget", "Tools", new BigDecimal("5.00"), 0);

        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getName()).isEqualTo("Widget");
        assertThat(response.getCategory()).isEqualTo("Tools");
        assertThat(response.getPrice()).isEqualByComparingTo("5.00");
        assertThat(response.getStock()).isZero();
    }
}
