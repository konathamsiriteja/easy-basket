package com.easybasket.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Product representation returned to clients")
public class ProductResponse {

    @Schema(description = "Generated product identifier", example = "1")
    private Long id;

    @Schema(description = "Product name", example = "Wireless Mouse")
    private String name;

    @Schema(description = "Product category", example = "Electronics")
    private String category;

    @Schema(description = "Product price", example = "19.99")
    private BigDecimal price;

    @Schema(description = "Available stock quantity", example = "100")
    private Integer stock;

    public ProductResponse() {
    }

    public ProductResponse(Long id, String name, String category, BigDecimal price, Integer stock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
