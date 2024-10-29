package com.hfsolution.feature.stockmanagement.service.product;

import org.springframework.stereotype.Service;

import com.hfsolution.app.dto.SearchRequestDTO;
import com.hfsolution.feature.stockmanagement.dto.request.ProductRequest;

@Service
public interface ProductService {

    public Object search(SearchRequestDTO request); 
    public Object addProduct(ProductRequest productRequest);
    public Object deleteProductById(Long id);
    public Object updateProductById(Long id , ProductRequest productRequest);

    
} 
