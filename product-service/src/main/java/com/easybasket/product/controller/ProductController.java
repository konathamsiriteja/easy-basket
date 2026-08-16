package com.easybasket.product.controller;

import com.easybasket.product.dto.ErrorResponse;
import com.easybasket.product.dto.ProductRequest;
import com.easybasket.product.dto.ProductResponse;
import com.easybasket.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create a new product", description = "Creates and persists a new product; returns the created product without a Location header.")
    @ApiResponse(responseCode = "201", description = "Product created",
            content = @Content(schema = @Schema(implementation = ProductResponse.class)))
    @ApiResponse(responseCode = "400", description = "Validation failure",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
