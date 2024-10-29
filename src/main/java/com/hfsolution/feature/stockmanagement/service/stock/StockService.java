package com.hfsolution.feature.stockmanagement.service.stock;

import org.springframework.stereotype.Service;

import com.hfsolution.app.dto.SearchRequestDTO;
import com.hfsolution.feature.stockmanagement.dto.request.StockRequest;

@Service
public interface StockService {

    
    public Object searchStock(SearchRequestDTO request);  
    public Object addStock(StockRequest productRequest);
    public Object deleteStockByProductId(Long id);
    public Object updateStockByProductId(Long id , StockRequest productRequest);   
    
} 
