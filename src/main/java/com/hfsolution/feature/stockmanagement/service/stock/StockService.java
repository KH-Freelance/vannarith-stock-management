package com.hfsolution.feature.stockmanagement.service.stock;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hfsolution.app.dto.BaseEntityResponseDto;

import com.hfsolution.feature.stockmanagement.dto.request.stock.StockRequest;
import com.hfsolution.feature.stockmanagement.dto.request.stock.StockUpdateRequest;

@Service
public interface StockService {

    public Object search(String  q, int pageNo, int pageSize, Sort.Direction sort, String sortByColum);
    public void export(String q);
    public Object importData(MultipartFile file);
    public Object addStock(StockRequest productRequest);
    public Object updateStock(Long id , StockUpdateRequest productRequest);   
    public Object deleteStockById(Long id);
    
} 
