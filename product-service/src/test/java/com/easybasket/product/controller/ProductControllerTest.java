package com.easybasket.product.controller;

import com.easybasket.product.dto.ProductRequest;
import com.easybasket.product.dto.ProductResponse;
import com.easybasket.product.exception.GlobalExceptionHandler;
import com.easybasket.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    void createProduct_validRequest_returns201WithBodyAndNoLocationHeader() throws Exception {
        ProductRequest request = new ProductRequest("Wireless Mouse", "Electronics", new BigDecimal("19.99"), 100);
        ProductResponse response = new ProductResponse(1L, "Wireless Mouse", "Electronics", new BigDecimal("19.99"), 100);
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Wireless Mouse"))
                .andExpect(jsonPath("$.category").value("Electronics"))
                .andExpect(jsonPath("$.price").value(19.99))
                .andExpect(jsonPath("$.stock").value(100));

        verify(productService).createProduct(any(ProductRequest.class));
    }

    @Test
    void createProduct_missingName_returns400() throws Exception {
        ProductRequest request = new ProductRequest(null, "Electronics", new BigDecimal("19.99"), 100);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").isArray());

        verifyNoInteractions(productService);
    }

    @Test
    void createProduct_missingCategory_returns400() throws Exception {
        ProductRequest request = new ProductRequest("Wireless Mouse", null, new BigDecimal("19.99"), 100);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void createProduct_missingPrice_returns400() throws Exception {
        ProductRequest request = new ProductRequest("Wireless Mouse", "Electronics", null, 100);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void createProduct_missingStock_returns400() throws Exception {
        ProductRequest request = new ProductRequest("Wireless Mouse", "Electronics", new BigDecimal("19.99"), null);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void createProduct_zeroPrice_returns400() throws Exception {
        ProductRequest request = new ProductRequest("Wireless Mouse", "Electronics", BigDecimal.ZERO, 100);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void createProduct_negativePrice_returns400() throws Exception {
        ProductRequest request = new ProductRequest("Wireless Mouse", "Electronics", new BigDecimal("-5.00"), 100);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void createProduct_priceWithMoreThanTwoDecimals_returns400() throws Exception {
        ProductRequest request = new ProductRequest("Wireless Mouse", "Electronics", new BigDecimal("19.999"), 100);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void createProduct_negativeStock_returns400() throws Exception {
        ProductRequest request = new ProductRequest("Wireless Mouse", "Electronics", new BigDecimal("19.99"), -1);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void createProduct_oversizedName_returns400() throws Exception {
        ProductRequest request = new ProductRequest("a".repeat(256), "Electronics", new BigDecimal("19.99"), 100);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void createProduct_oversizedCategory_returns400() throws Exception {
        ProductRequest request = new ProductRequest("Wireless Mouse", "b".repeat(256), new BigDecimal("19.99"), 100);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void createProduct_zeroStock_isValidBoundaryAndReturns201() throws Exception {
        ProductRequest request = new ProductRequest("Widget", "Tools", new BigDecimal("5.00"), 0);
        ProductResponse response = new ProductResponse(2L, "Widget", "Tools", new BigDecimal("5.00"), 0);
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stock").value(0));
    }
}
