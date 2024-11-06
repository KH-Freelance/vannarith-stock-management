package com.hfsolution.feature.stockmanagement.service.product;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.SearchRequestDTO;
import com.hfsolution.feature.stockmanagement.dto.request.product.ProductRequest;
import com.hfsolution.feature.stockmanagement.dto.request.product.ProductUpdateRequest;

@Service
public interface ProductService {

    public Object search(SearchRequestDTO request);
    public Object search(String  q, int pageNo, int pageSize, Sort.Direction sort, String sortByColum);
    public void export(String q);
    public Object importData(MultipartFile file);
    public Object addProduct(ProductRequest productRequest);
    public Object deleteProductById(Long id);
    public Object updateProductById(Long id , ProductUpdateRequest productUpdateRequest);

    
}  
