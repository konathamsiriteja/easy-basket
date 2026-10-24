package com.easybasket.product.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductNotFoundExceptionTest {

    @Test
    void constructor_setsProductIdAndMessage() {
        ProductNotFoundException ex = new ProductNotFoundException(42L);

        assertThat(ex.getProductId()).isEqualTo(42L);
        assertThat(ex.getMessage()).isEqualTo("Product not found with id: 42");
    }
}
