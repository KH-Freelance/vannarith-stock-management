package com.hfsolution.feature.stockmanagement.service.product;

import org.springframework.stereotype.Service;

import com.hfsolution.app.dto.SearchRequestDTO;
import com.hfsolution.feature.stockmanagement.dto.request.product.ProductRequest;
import com.hfsolution.feature.stockmanagement.dto.request.product.ProductUpdateRequest;

@Service
public interface ProductService {

    public Object search(SearchRequestDTO request);
    public void export();
    public Object addProduct(ProductRequest productRequest);
    public Object deleteProductById(Long id);
    public Object updateProductById(Long id , ProductUpdateRequest productUpdateRequest);

    
} 
