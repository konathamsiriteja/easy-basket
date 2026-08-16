package com.easybasket.product.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    void noArgsConstructor_allowsSettersToPopulateFields() {
        ErrorResponse error = new ErrorResponse();
        Instant now = Instant.now();
        error.setStatus(400);
        error.setError("Bad Request");
        error.setMessage("Validation failed");
        error.setFieldErrors(List.of("name: must not be blank"));
        error.setTimestamp(now);

        assertThat(error.getStatus()).isEqualTo(400);
        assertThat(error.getError()).isEqualTo("Bad Request");
        assertThat(error.getMessage()).isEqualTo("Validation failed");
        assertThat(error.getFieldErrors()).containsExactly("name: must not be blank");
        assertThat(error.getTimestamp()).isEqualTo(now);
    }

    @Test
    void allArgsConstructor_populatesFields() {
        Instant now = Instant.now();
        ErrorResponse error = new ErrorResponse(500, "Internal Server Error", "boom", List.of(), now);

        assertThat(error.getStatus()).isEqualTo(500);
        assertThat(error.getError()).isEqualTo("Internal Server Error");
        assertThat(error.getMessage()).isEqualTo("boom");
        assertThat(error.getFieldErrors()).isEmpty();
        assertThat(error.getTimestamp()).isEqualTo(now);
    }
}
