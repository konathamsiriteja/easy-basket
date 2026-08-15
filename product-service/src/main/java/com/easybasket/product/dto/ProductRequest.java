package com.easybasket.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Payload for creating a new product")
public class ProductRequest {

    @NotBlank(message = "name is mandatory")
    @Size(max = 255, message = "name must not exceed 255 characters")
    @Schema(description = "Product name", example = "Wireless Mouse")
    private String name;

    @NotBlank(message = "category is mandatory")
    @Size(max = 255, message = "category must not exceed 255 characters")
    @Schema(description = "Product category", example = "Electronics")
    private String category;

    @NotNull(message = "price is mandatory")
    @Positive(message = "price must be strictly positive")
    @Digits(integer = 10, fraction = 2, message = "price must have at most 2 decimal places")
    @Schema(description = "Product price (USD, 2 decimal places)", example = "19.99")
    private BigDecimal price;

    @NotNull(message = "stock is mandatory")
    @PositiveOrZero(message = "stock must not be negative")
    @Schema(description = "Available stock quantity", example = "100")
    private Integer stock;

    public ProductRequest() {
    }

    public ProductRequest(String name, String category, BigDecimal price, Integer stock) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
