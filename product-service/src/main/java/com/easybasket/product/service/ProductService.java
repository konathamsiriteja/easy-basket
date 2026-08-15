package com.easybasket.product.service;

import com.easybasket.product.dto.ProductRequest;
import com.easybasket.product.dto.ProductResponse;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);
}
