package com.easybasket.product.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void validRequest_hasNoViolations() {
        ProductRequest request = new ProductRequest("Wireless Mouse", "Electronics", new BigDecimal("19.99"), 100);

        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankName_isRejected() {
        ProductRequest request = new ProductRequest("", "Electronics", new BigDecimal("19.99"), 100);

        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void blankCategory_isRejected() {
        ProductRequest request = new ProductRequest("Wireless Mouse", "", new BigDecimal("19.99"), 100);

        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("category"));
    }

    @Test
    void oversizedName_isRejected() {
        String tooLong = "a".repeat(256);
        ProductRequest request = new ProductRequest(tooLong, "Electronics", new BigDecimal("19.99"), 100);

        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void oversizedCategory_isRejected() {
        String tooLong = "b".repeat(256);
        ProductRequest request = new ProductRequest("Wireless Mouse", tooLong, new BigDecimal("19.99"), 100);

        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("category"));
    }

    @Test
    void nullPrice_isRejected() {
        ProductRequest request = new ProductRequest("Wireless Mouse", "Electronics", null, 100);

        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("price"));
    }

    @Test
    void zeroPrice_isRejected() {
        ProductRequest request = new ProductRequest("Wireless Mouse", "Electronics", BigDecimal.ZERO, 100);

        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("price"));
    }

    @Test
    void negativePrice_isRejected() {
        ProductRequest request = new ProductRequest("Wireless Mouse", "Electronics", new BigDecimal("-1.00"), 100);

        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("price"));
    }

    @Test
    void priceWithTooManyDecimals_isRejected() {
        ProductRequest request = new ProductRequest("Wireless Mouse", "Electronics", new BigDecimal("19.999"), 100);

        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("price"));
    }

    @Test
    void nullStock_isRejected() {
        ProductRequest request = new ProductRequest("Wireless Mouse", "Electronics", new BigDecimal("19.99"), null);

        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("stock"));
    }

    @Test
    void negativeStock_isRejected() {
        ProductRequest request = new ProductRequest("Wireless Mouse", "Electronics", new BigDecimal("19.99"), -5);

        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("stock"));
    }

    @Test
    void zeroStock_isValidBoundary() {
        ProductRequest request = new ProductRequest("Wireless Mouse", "Electronics", new BigDecimal("19.99"), 0);

        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }
}
