package com.hfsolution.feature.stockmanagement.service.stock;

import org.springframework.stereotype.Service;
import com.hfsolution.app.dto.SearchRequestDTO;
import com.hfsolution.feature.stockmanagement.dto.request.stock.StockRequest;
import com.hfsolution.feature.stockmanagement.dto.request.stock.StockUpdateRequest;

@Service
public interface StockService {

    public Object searchStock(SearchRequestDTO request);  
    public Object addStock(StockRequest productRequest);
    public Object updateStock(Long id , StockUpdateRequest productRequest);   
    public Object deleteStockById(Long id);
    
} 
